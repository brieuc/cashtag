package com.brieuc.cashtag.service;

import java.time.LocalDate;
import java.util.List;

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
    public List<Rate> getRatesBySourceCurrency(@NotNull String sourceCurrencyCode) {
        return rateRepository.findBySourceCurrencyCode(sourceCurrencyCode);
    }

    @Override
    public List<Rate> getRatesByTargetCurrency(@NotNull String targetCurrencyCode) {
        return rateRepository.findByTargetCurrencyCode(targetCurrencyCode);
    }

    @Override
    public Rate getRateByCurrenciesAndDate(@NotNull String sourceCurrencyCode, @NotNull String targetCurrencyCode, @NotNull LocalDate valueDate) {
        return rateRepository.findClosestRateBefore(sourceCurrencyCode, targetCurrencyCode, valueDate)
            .orElseThrow(() -> new EntityNotFoundException("not found for source currency " + sourceCurrencyCode + ", target currency " + targetCurrencyCode + " on " + valueDate));
    }

    @Override
    public Rate save(@NotNull Rate rate) {
        if (rate.getSourceCurrency().getCode().equals("DYN") || rate.getTargetCurrency().getCode().equals("DYN")) {
            throw new RuntimeException("Not possible to add rate for dynamic currency");
        }
        return rateRepository.save(rate);
    }

    @Override
    public Rate update(@NotNull Rate detachedRate) {
        if (detachedRate.getSourceCurrency().getCode().equals("DYN") || detachedRate.getTargetCurrency().getCode().equals("DYN")) {
            throw new RuntimeException("Not possible to add rate for dynamic currency");
        }
        Rate rate = getById(detachedRate.getId());
        rate.setSourceCurrency(detachedRate.getSourceCurrency());
        rate.setTargetCurrency(detachedRate.getTargetCurrency());
        rate.setValueDate(detachedRate.getValueDate());
        rate.setRate(detachedRate.getRate());
        return rateRepository.save(rate);
    }

    @Override
    public void delete(@NotNull Rate rate) {
        rateRepository.delete(rate);
    }


}
