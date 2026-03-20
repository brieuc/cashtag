package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Recurrence;
import jakarta.validation.constraints.NotNull;
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
}
