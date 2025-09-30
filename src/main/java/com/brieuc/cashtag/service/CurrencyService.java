package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    public Optional<Currency> findByCode(String code) {
        return currencyRepository.findById(code);
    }

    public Currency save(Currency currency) {
        return currencyRepository.save(currency);
    }

    public void deleteByCode(String code) {
        currencyRepository.deleteById(code);
    }
}