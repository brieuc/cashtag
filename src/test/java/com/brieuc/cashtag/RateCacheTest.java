package com.brieuc.cashtag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.RateRepository;
import com.brieuc.cashtag.service.RateCache;
import com.brieuc.cashtag.service.event.RatesChangedEvent;

@ExtendWith(MockitoExtension.class)
public class RateCacheTest {

      private static final Currency CHF = new Currency("CHF", true);
      private static final Currency EUR = new Currency("EUR", false);
      private static final Currency USD = new Currency("USD", false);

      @InjectMocks
      private RateCache rateCache;

      @Mock
      private RateRepository rateRepository;

      private static Rate rate(Currency source, Currency target, LocalDate valueDate, String value) {
            return new Rate(null, source, target, valueDate, new BigDecimal(value));
      }

      @Test
      void ShouldReturnTheRateOfTheExactValueDate() {
            when(rateRepository.findAll()).thenReturn(List.of(
                  rate(EUR, CHF, LocalDate.of(2025, 1, 1), "0.9500"),
                  rate(EUR, CHF, LocalDate.of(2025, 2, 1), "0.9400")));
            rateCache.onApplicationReady();

            assertEquals(new BigDecimal("0.9400"), rateCache.getRate("EUR", "CHF", LocalDate.of(2025, 2, 1)));
      }

      @Test
      void ShouldReturnTheClosestRateBeforeTheValueDate() {
            /*
            Rates exist on 2025-01-01 and 2025-02-01
            Requested date 2025-01-20 falls in between
            The 2025-01-01 rate must be used, not the later one
            */
            when(rateRepository.findAll()).thenReturn(List.of(
                  rate(EUR, CHF, LocalDate.of(2025, 1, 1), "0.9500"),
                  rate(EUR, CHF, LocalDate.of(2025, 2, 1), "0.9400")));
            rateCache.onApplicationReady();

            assertEquals(new BigDecimal("0.9500"), rateCache.getRate("EUR", "CHF", LocalDate.of(2025, 1, 20)));
            assertEquals(new BigDecimal("0.9400"), rateCache.getRate("EUR", "CHF", LocalDate.of(2026, 1, 1)));
      }

      @Test
      void ShouldNotMixRatesOfDifferentCurrencyPairs() {
            when(rateRepository.findAll()).thenReturn(List.of(
                  rate(EUR, CHF, LocalDate.of(2025, 1, 1), "0.9500"),
                  rate(USD, CHF, LocalDate.of(2025, 1, 1), "0.8800")));
            rateCache.onApplicationReady();

            assertEquals(new BigDecimal("0.9500"), rateCache.getRate("EUR", "CHF", LocalDate.of(2025, 1, 1)));
            assertEquals(new BigDecimal("0.8800"), rateCache.getRate("USD", "CHF", LocalDate.of(2025, 1, 1)));
      }

      @Test
      void ShouldThrowIfNoRateBeforeTheValueDate() {
            when(rateRepository.findAll()).thenReturn(List.of(
                  rate(EUR, CHF, LocalDate.of(2025, 1, 1), "0.9500")));
            rateCache.onApplicationReady();

            assertThrows(EntityNotFoundException.class, () -> rateCache.getRate("EUR", "CHF", LocalDate.of(2024, 12, 31)));
      }

      @Test
      void ShouldThrowIfNoRateForTheCurrencyPair() {
            when(rateRepository.findAll()).thenReturn(List.of(
                  rate(EUR, CHF, LocalDate.of(2025, 1, 1), "0.9500")));
            rateCache.onApplicationReady();

            // Inverse pair is not stored, ComputationService handles the inversion itself.
            assertThrows(EntityNotFoundException.class, () -> rateCache.getRate("CHF", "EUR", LocalDate.of(2025, 1, 1)));
            assertThrows(EntityNotFoundException.class, () -> rateCache.getRate("USD", "CHF", LocalDate.of(2025, 1, 1)));
      }

      @Test
      void ShouldThrowIfNotLoaded() {
            assertThrows(EntityNotFoundException.class, () -> rateCache.getRate("EUR", "CHF", LocalDate.of(2025, 1, 1)));
      }

      @Test
      void ShouldReloadWhenRatesChanged() {
            /*
            The cache is loaded with a single EUR rate
            A new rate is saved and RatesChangedEvent is received
            The cache must expose the new rate and keep the existing one
            */
            when(rateRepository.findAll())
                  .thenReturn(List.of(
                        rate(EUR, CHF, LocalDate.of(2025, 1, 1), "0.9500")))
                  .thenReturn(List.of(
                        rate(EUR, CHF, LocalDate.of(2025, 1, 1), "0.9500"),
                        rate(EUR, CHF, LocalDate.of(2025, 2, 1), "0.9400")));
            rateCache.onApplicationReady();
            assertEquals(new BigDecimal("0.9500"), rateCache.getRate("EUR", "CHF", LocalDate.of(2025, 2, 15)));

            rateCache.onRatesChanged(new RatesChangedEvent());

            assertEquals(new BigDecimal("0.9400"), rateCache.getRate("EUR", "CHF", LocalDate.of(2025, 2, 15)));
            assertEquals(new BigDecimal("0.9500"), rateCache.getRate("EUR", "CHF", LocalDate.of(2025, 1, 15)));
      }

      @Test
      void ShouldDropDeletedRatesOnReload() {
            when(rateRepository.findAll())
                  .thenReturn(List.of(
                        rate(EUR, CHF, LocalDate.of(2025, 1, 1), "0.9500"),
                        rate(USD, CHF, LocalDate.of(2025, 1, 1), "0.8800")))
                  .thenReturn(List.of(
                        rate(EUR, CHF, LocalDate.of(2025, 1, 1), "0.9500")));
            rateCache.onApplicationReady();
            assertEquals(new BigDecimal("0.8800"), rateCache.getRate("USD", "CHF", LocalDate.of(2025, 1, 1)));

            rateCache.onRatesChanged(new RatesChangedEvent());

            assertThrows(EntityNotFoundException.class, () -> rateCache.getRate("USD", "CHF", LocalDate.of(2025, 1, 1)));
      }
}
