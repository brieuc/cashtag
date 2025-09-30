package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Period;
import com.brieuc.cashtag.repository.PeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeriodService {

    private final PeriodRepository periodRepository;

    public List<Period> findAll() {
        return periodRepository.findAll();
    }

    public Optional<Period> findById(Long id) {
        return periodRepository.findById(id);
    }

    public Optional<Period> findPeriodByDate(LocalDate date) {
        return periodRepository.findPeriodByDate(date);
    }

    public Period save(Period period) {
        return periodRepository.save(period);
    }

    public void deleteById(Long id) {
        periodRepository.deleteById(id);
    }
}