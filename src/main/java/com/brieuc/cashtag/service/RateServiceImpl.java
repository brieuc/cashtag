package com.brieuc.cashtag.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.RateRepository;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {

    private final RateRepository rateRepository;

    @Override
    public Page<Rate> getRates(@NotNull Specification<Rate> specification, @NotNull Pageable pageable) {
        return rateRepository.findAll(specification, pageable);
    }

    @Override
    public Rate getById(@NotNull Long id) {
        return rateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("rate not found for id " + id));
    }

    @Override
    public List<Rate> getRatesByCurrency(@NotNull String currencyCode) {
        return rateRepository.findByCurrencyCode(currencyCode);
    }

    @Override
    public Rate getRateByCurrencyAndDate(@NotNull String currencyCode, @NotNull LocalDate valueDate) {
        return rateRepository.findByCurrencyCodeAndValueDate(currencyCode, valueDate).orElseThrow(() -> new EntityNotFoundException("not found for currency code " + currencyCode + " and value date " + valueDate));
    }

    @Override
    public Rate save(@NotNull Rate rate) {
        return rateRepository.save(rate);
    }

    @Override
    public void delete(@NotNull Rate rate) {
        rateRepository.delete(rate);
    }


}
