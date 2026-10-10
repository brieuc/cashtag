package com.brieuc.cashtag.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.RateRepository;
import com.brieuc.cashtag.service.event.RatesChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * In-memory snapshot of all the rates, indexed by currency pair and value date.
 * Loaded when the application is ready and fully reloaded each time a rate changes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateCache {

      private record CurrencyPair(String sourceCurrencyCode, String targetCurrencyCode) {}

      private final RateRepository rateRepository;

      // Immutable snapshot, replaced as a whole on reload.
      private volatile Map<CurrencyPair, NavigableMap<LocalDate, BigDecimal>> rates = Map.of();

      @EventListener(ApplicationReadyEvent.class)
      public void onApplicationReady() {
            reload();
      }

      @TransactionalEventListener
      public void onRatesChanged(RatesChangedEvent event) {
            reload();
      }

      /**
       * Same semantic as RateRepository.findClosestRateBefore: the rate with the most recent
       * value date on or before the given date.
       */
      public BigDecimal getRate(String sourceCurrencyCode, String targetCurrencyCode, LocalDate valueDate) {
            NavigableMap<LocalDate, BigDecimal> ratesByDate = rates.get(new CurrencyPair(sourceCurrencyCode, targetCurrencyCode));
            Map.Entry<LocalDate, BigDecimal> entry = ratesByDate == null ? null : ratesByDate.floorEntry(valueDate);
            if (entry == null)
                  throw new EntityNotFoundException("not found for source currency " + sourceCurrencyCode + ", target currency " + targetCurrencyCode + " on " + valueDate);
            return entry.getValue();
      }

      private synchronized void reload() {
            Map<CurrencyPair, NavigableMap<LocalDate, BigDecimal>> newRates = new HashMap<>();
            for (Rate rate : rateRepository.findAll()) {
                  CurrencyPair pair = new CurrencyPair(rate.getSourceCurrency().getCode(), rate.getTargetCurrency().getCode());
                  newRates.computeIfAbsent(pair, p -> new TreeMap<>()).put(rate.getValueDate(), rate.getRate());
            }
            newRates.replaceAll((pair, ratesByDate) -> Collections.unmodifiableNavigableMap(ratesByDate));
            rates = Map.copyOf(newRates);
            log.info("Rate cache loaded: {} currency pairs", rates.size());
      }
}
