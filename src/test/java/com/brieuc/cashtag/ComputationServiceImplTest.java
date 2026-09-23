package com.brieuc.cashtag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.mapper.EntrySpecificationMapperImpl;
import com.brieuc.cashtag.service.ComputationServiceImpl;
import com.brieuc.cashtag.service.CurrencyServiceImpl;
import com.brieuc.cashtag.service.EntryServiceImpl;
import com.brieuc.cashtag.service.RateServiceImpl;
import com.brieuc.cashtag.service.helper.ComputeResult;

@ExtendWith(MockitoExtension.class)
public class ComputationServiceImplTest {
      
      @InjectMocks
      private ComputationServiceImpl computationService;

      @Mock
      private EntryServiceImpl entryService;

      @Mock
      private CurrencyServiceImpl currencyService;

      @Mock
      private RateServiceImpl rateService;

      @Spy
      private EntrySpecificationMapperImpl entrySpecificationMapper;


      @Test
      void ShouldComputeWithSameCurrencyBetweenEntryAndTargetCurrency() {
            /*
            The entry contains 1000.10 CHF
            CHF is the system's reference currency
            User requests the total in CHF
            Entry currency matches target currency (both CHF)
            No conversion needed
            Test verifies 1 entry is counted
            Test verifies the total amount is 1000.10 (unchanged)
            */

            Entry entry = Entry.builder()
                  .accountingDate(LocalDateTime.now())
                  .title("Entry Test Title")
                  .description("Entry Test Description")
                  .amount(BigDecimal.valueOf(1000.10))
                  .currency(new Currency("CHF", true))
                  .build();

            //Page<Entry> page = new PageImpl<>(List.of(entry));
            //when(entryService.getEntries(any(), any())).thenReturn(page);

            ComputationRequestDto computationRequestDto = new ComputationRequestDto(
                        LocalDateTime.of(2020, 1, 1, 0, 0),
                        LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                        null, null, null, null, "CHF");

            ComputeResult computeResult = computationService.computeSum(List.of(entry), computationRequestDto.targetCurrencyCode());

            assertEquals(1, computeResult.numberOfEntries());
            assertEquals(1000.1, computeResult.totalAmount().doubleValue());
      }

      @Test
      void ShouldReturnTheSameCurrencyBetweenRequestAndResponse() {
            /*
            The entry contains 1000.10 CHF
            CHF is the system's reference currency
            User requests the total in CHF
            Entry currency matches target currency (both CHF)
            No conversion needed
            Test verifies the response currency code matches the request currency code
            */

            // Arrange
            Entry entry = Entry.builder()
                  .accountingDate(LocalDateTime.now())
                  .title("Entry Test Title")
                  .description("Entry Test Description")
                  .amount(BigDecimal.valueOf(1000.10))
                  .currency(new Currency("CHF", true))
                  .build();

            //Page<Entry> page = new PageImpl<>(List.of(entry));
            //when(entryService.getEntries(any(), any())).thenReturn(page);

            // Act
            ComputationRequestDto computationRequestDto = new ComputationRequestDto(
                        LocalDateTime.of(2020, 1, 1, 0, 0),
                        LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                        null, null, null, null, "CHF");

            ComputeResult computeResult = computationService.computeSum(List.of(entry), computationRequestDto.targetCurrencyCode());

            // Assert
            assertEquals(computationRequestDto.targetCurrencyCode(), computeResult.targetCurrencyCode());
      }

      @Test
      void ShouldConvertEntriesWhenInDifferentCurrencyThanTargetCurrency() {
            /*
            The entry contains 1000 EUR
            CHF is the system's reference currency
            User requests the total in CHF (the reference currency)
            System retrieves the EUR -> CHF rate: 0.5 CHF for 1 EUR
            System calculates: 1000 / 2 = 500 CHF
            */

            // Arrange
            Entry entry = Entry.builder()
            .accountingDate(LocalDateTime.now())
            .amount(BigDecimal.valueOf(1000))
            .currency(new Currency("EUR", false))
            .build();

            //Page<Entry> page = new PageImpl<>(List.of(entry));
            //when(entryService.getEntries(any(), any())).thenReturn(page);
            when(currencyService.getReferenceCurrency()).thenReturn(new Currency("CHF", true));
            when(rateService.getRateByCurrenciesAndDate(any(), any(), any()))
                  .thenReturn(new Rate(null, null, null, null, BigDecimal.valueOf(0.5)));
            

            // Act
            ComputationRequestDto computationRequestDto = new ComputationRequestDto(
                        LocalDateTime.of(2020, 1, 1, 0, 0),
                        LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                        null, null, null, null, "CHF");
            ComputeResult computeResult = computationService.computeSum(List.of(entry), computationRequestDto.targetCurrencyCode());

            // Assert
            assertEquals(1000 / 2, computeResult.totalAmount().doubleValue());
      }

      @Test
      void ShouldThrowIfTargetCurrencyNotMatchEntryCurrencyAndIsNotReferenceCurrency() {

            /*
            The entry contains 1000 EUR
            CHF is the system's reference currency
            User requests the total in USD
            EUR is neither the target currency (USD) nor the reference currency (CHF)
            System cannot convert EUR to USD without going through CHF first
            Test verifies that EntityNotFoundException is thrown
            This scenario requires a EUR→CHF rate that doesn't exist in the mock
            */

            // Arrange
            Entry entry = Entry.builder()
            .accountingDate(LocalDateTime.now())
            .amount(BigDecimal.valueOf(1000))
            .currency(new Currency("EUR", false))
            .build();

            //Page<Entry> page = new PageImpl<>(List.of(entry));
            //when(entryService.getEntries(any(), any())).thenReturn(page);
            when(currencyService.getReferenceCurrency()).thenReturn(new Currency("CHF", true));

            // Act & Assert
            ComputationRequestDto computationRequestDto = new ComputationRequestDto(
                        LocalDateTime.of(2020, 1, 1, 0, 0),
                        LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                        null, null, null, null, "USD");
            assertThrows(EntityNotFoundException.class, () -> computationService.computeSum(List.of(entry), computationRequestDto.targetCurrencyCode()));

      }

      @Test
      void ShouldComputeIfTargetCurrencyIsNotReferenceCurrencyAndEntryCurrencyNotTargetCurrencyButAtLeastReferenceCurrency() {
            /* 
            The entry contains 1000 CHF
            CHF is the system's reference currency
            User requests the total in EUR
            System retrieves the EUR to CHF rate: 0.5
            System calculates: 1000 x 2 = 2000 EUR
            */

            // Arrange
            Entry entry = Entry.builder()
            .accountingDate(LocalDateTime.now())
            .amount(BigDecimal.valueOf(1000))
            .currency(new Currency("CHF", false))
            .build();

            //Page<Entry> page = new PageImpl<>(List.of(entry));
            //when(entryService.getEntries(any(), any())).thenReturn(page);
            when(currencyService.getReferenceCurrency()).thenReturn(new Currency("CHF", true));
            when(rateService.getRateByCurrenciesAndDate(any(), any(), any()))
                  .thenReturn(new Rate(null, null, null, null, BigDecimal.valueOf(0.5)));

            // Act
            ComputationRequestDto computationRequestDto = new ComputationRequestDto(
                        LocalDateTime.of(2020, 1, 1, 0, 0),
                        LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                        null, null, null, null, "EUR");

            ComputeResult computeResult = computationService.computeSum(List.of(entry), computationRequestDto.targetCurrencyCode());

            // Assert
            assertEquals(1000 * 2, computeResult.totalAmount().doubleValue());
      }
}
