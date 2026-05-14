package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Frequency;
import com.brieuc.cashtag.entity.Recurrence;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.RecurrenceRepository;
import com.brieuc.cashtag.service.helper.SimulatedEntry;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    public Page<Recurrence> getRecurrences(@NotNull Specification<Recurrence> specification,
            @NotNull Pageable pageable) {
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

    @Override
    public List<SimulatedEntry> simulateEntries(LocalDateTime fromDate, LocalDateTime toDate) {

        ArrayList<SimulatedEntry> simulatedEntries = new ArrayList<>();
        List<Recurrence> recurrences = recurrenceRepository.findAll().stream()
                .filter(r -> r.getFrequency() != Frequency.NONE).toList();
        for (Recurrence recurrence : recurrences) {
            Optional<LocalDateTime> computedAccountingDate = getIntervalDate(recurrence, fromDate, toDate);
            computedAccountingDate.ifPresent(date -> {
                simulatedEntries.add(new SimulatedEntry(date, recurrence.getTitle(), recurrence.getDescription(),
                        recurrence.getAmount(), recurrence.getCurrency().getCode(),
                        new ArrayList<>(recurrence.getTags())));
            });
        }
        simulatedEntries.sort(Comparator.comparing(SimulatedEntry::accountingDate, Comparator.reverseOrder()));
        return simulatedEntries;
    }

    /**
     * 
     * @param initialAccountingDate
     * @param fromDate
     * @param toDate
     * @return The date included in the interval or an empty optional if the
     *         computed accounting date exceed the interval end date
     */
    private Optional<LocalDateTime> getIntervalDate(Recurrence recurrence, LocalDateTime fromDate, LocalDateTime toDate) {
        if (recurrence.getFrequency() == Frequency.NONE)
            return Optional.empty();
        LocalDateTime currentDate = recurrence.getStartDate();
        do {
            if (!currentDate.isBefore(fromDate) && !currentDate.isAfter(toDate)) {
                return Optional.of(currentDate);
            }
            currentDate = switch (recurrence.getFrequency()) {
                case DAILY -> currentDate.plusDays(1);
                case WEEKLY -> currentDate.plusWeeks(1);
                case MONTHLY -> currentDate.plusMonths(1);
                case QUARTERLY -> currentDate.plusMonths(3);
                case YEARLY -> currentDate.plusYears(1);
                default -> throw new RuntimeException("unsupported frequency");
            };
        } while (!currentDate.isAfter(toDate));

        return Optional.empty();
    }

}
