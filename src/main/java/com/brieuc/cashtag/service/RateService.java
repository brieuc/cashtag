package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RateService {

    private final RateRepository rateRepository;

    public List<Rate> findAll() {
        return rateRepository.findAll();
    }

    public Optional<Rate> findById(Long id) {
        return rateRepository.findById(id);
    }

    public List<Rate> findByCurrency(String currencyCode) {
        return rateRepository.findByCurrencyCode(currencyCode);
    }

    public Optional<Rate> findByCurrencyAndDate(String currencyCode, LocalDate valueDate) {
        return rateRepository.findByCurrencyCodeAndValueDate(currencyCode, valueDate);
    }

    public Rate save(Rate rate) {
        return rateRepository.save(rate);
    }

    public void deleteById(Long id) {
        rateRepository.deleteById(id);
    }
}