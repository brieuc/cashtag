package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Rate;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public interface RateService {
    Page<Rate> getRates(@NotNull Specification<Rate> specification, @NotNull Pageable pageable);
    Rate getById(@NotNull Long id);
    List<Rate> getRatesBySourceCurrency(@NotNull String sourceCurrencyCode);
    List<Rate> getRatesByTargetCurrency(@NotNull String targetCurrencyCode);
    Rate getRateByCurrenciesAndDate(@NotNull String sourceCurrencyCode, @NotNull String targetCurrencyCode, @NotNull LocalDate valueDate);
    Rate save(@NotNull Rate rate);
    Rate update(@NotNull Rate rate);
    void delete(@NotNull Rate rate);
}