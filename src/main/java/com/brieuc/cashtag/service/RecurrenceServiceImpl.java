package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Recurrence;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.RecurrenceRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class RecurrenceServiceImpl implements RecurrenceService {

    private final RecurrenceRepository recurrenceRepository;

    @Override
    public Page<Recurrence> getRecurrences(@NotNull Specification<Recurrence> specification, @NotNull Pageable pageable) {
        return recurrenceRepository.findAll(specification, pageable);
    }

    @Override
    public Recurrence getById(@NotNull Long id) {
        return recurrenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("no recurrence id " + id + " was found"));
    }

    @Override
    public Recurrence create(@NotNull Recurrence recurrence) {
        return recurrenceRepository.save(recurrence);
    }

    @Override
    public Recurrence update(@NotNull Recurrence detachedRecurrence) {
        Recurrence recurrence = getById(detachedRecurrence.getId());
        recurrence.setTitle(detachedRecurrence.getTitle());
        recurrence.setDescription(detachedRecurrence.getDescription());
        recurrence.setAmount(detachedRecurrence.getAmount());
        recurrence.setCurrency(detachedRecurrence.getCurrency());
        recurrence.setStartDate(detachedRecurrence.getStartDate());
        recurrence.setFrequency(detachedRecurrence.getFrequency());
        recurrence.setTags(detachedRecurrence.getTags());
        return recurrenceRepository.save(recurrence);
    }

    @Override
    public void delete(@NotNull Recurrence recurrence) {
        recurrenceRepository.delete(recurrence);
    }
}
