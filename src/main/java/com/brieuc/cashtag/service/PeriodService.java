package com.brieuc.cashtag.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.brieuc.cashtag.entity.Period;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Optional;

public interface PeriodService {
      Page<Period> getPeriods(@NotNull Specification<Period> specification, @NotNull Pageable pageable);
      Period create(@NotNull Period period);
      Period update(@NotNull Period period);
      Period getById(@NotNull Long id);
      void deleteById(@NotNull Long id);
      Optional<Period> findPeriodByDate(@NotNull LocalDate date);
}