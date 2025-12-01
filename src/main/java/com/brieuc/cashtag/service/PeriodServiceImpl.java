package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Period;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.PeriodRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class PeriodServiceImpl implements PeriodService {

    private final PeriodRepository periodRepository;

    @Override
    public Page<Period> getPeriods(@NotNull Specification<Period> specification, @NotNull Pageable pageable) {
        return periodRepository.findAll(specification, pageable);
    }

    @Override
    public Period getById(@NotNull Long id) {
        return periodRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No period id " + id + " was found"));
    }

    @Override
    public Period create(@NotNull Period period) {
        return periodRepository.save(period);
    }

    @Override
    public void deleteById(@NotNull Long id) {
        periodRepository.deleteById(id);
    }

    @Override
    public Period getPeriodById(@NotNull Long id) {
        return periodRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("no period found with id " + id));
    }

    @Override
    public Period update(@NotNull Period period) {
        return periodRepository.save(period);
    }

    @Override
    public Optional<Period> findPeriodByDate(@NotNull LocalDate date) {
        return periodRepository.findPeriodByDate(date);
    }
}