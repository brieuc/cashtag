package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.CurrencyRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    public List<Currency> getCurrencies() {
        // No need for the reference currency, mess up the list
        return currencyRepository.findAll().stream().filter(currency -> currency.getReference() == false).toList();
    }

    @Override
    public Currency getReferenceCurrency() {
        return currencyRepository.findByReferenceTrue().orElseThrow(() -> new EntityNotFoundException("No reference currency code was found"));
    }

    @Override
    public Currency getById(@NotNull String code) {
        return currencyRepository.findById(code).orElseThrow(() -> new EntityNotFoundException("No currency code " + code + " was found"));
    }

    @Override
    public Currency create(@NotNull Currency currency) {
        currency.setReference(false);
        return currencyRepository.save(currency);
    }

    @Override
    public void deleteById(@NotNull String code) {
        currencyRepository.deleteById(code);
    }

    @Override
    public Currency getCurrencyByCode(@NotNull String code) {
        return currencyRepository.findById(code).orElseThrow(() -> new EntityNotFoundException("no currency found with code " + code));
    }
}
