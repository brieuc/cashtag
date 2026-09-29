package com.brieuc.cashtag.service;

import java.util.List;

import com.brieuc.cashtag.entity.Currency;

import jakarta.validation.constraints.NotNull;

public interface CurrencyService {
      List<Currency> getCurrencies();
      Currency getCurrencyByCode(@NotNull String code);
      Currency create(@NotNull Currency currency);
      // No update for currency, only add new currency
      Currency getById(@NotNull String code);
      void deleteById(@NotNull String code);
      Currency getReferenceCurrency();
}