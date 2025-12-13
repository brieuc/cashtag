package com.brieuc.cashtag.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.brieuc.cashtag.entity.Currency;

import jakarta.validation.constraints.NotNull;

public interface CurrencyService {
      Page<Currency> getCurrencies(@NotNull Specification<Currency> specification, @NotNull Pageable pageable);
      Currency getCurrencyByCode(@NotNull String code);
      Currency create(@NotNull Currency currency);
      // No update for currency, only add new currency
      Currency getById(@NotNull String code);
      void deleteById(@NotNull String code);
      Currency getReferenceCurrency();
}