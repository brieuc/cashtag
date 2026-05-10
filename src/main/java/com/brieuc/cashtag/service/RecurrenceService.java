package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Frequency;
import com.brieuc.cashtag.entity.Recurrence;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.service.helper.SimulatedEntry;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface RecurrenceService {
    Page<Recurrence> getRecurrences(@NotNull Specification<Recurrence> specification, @NotNull Pageable pageable);
    Recurrence getById(@NotNull Long id);
    Recurrence create(@NotNull Recurrence recurrence);
    Recurrence update(@NotNull Recurrence recurrence);
    void delete(@NotNull Recurrence recurrence);
    List<SimulatedEntry> simulateEntries(LocalDate fromDate, LocalDate toDate);
}
