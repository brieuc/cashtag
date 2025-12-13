package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.CurrencyRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    public Page<Currency> getCurrencies(@NotNull Specification<Currency> specification, @NotNull Pageable pageable) {
        return currencyRepository.findAll(specification, pageable);
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
        // The refernece is supposed to be only for DYN currency
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
