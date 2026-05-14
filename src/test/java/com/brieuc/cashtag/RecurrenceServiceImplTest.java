package com.brieuc.cashtag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Frequency;
import com.brieuc.cashtag.entity.Recurrence;
import com.brieuc.cashtag.repository.RecurrenceRepository;
import com.brieuc.cashtag.service.RecurrenceServiceImpl;
import com.brieuc.cashtag.service.helper.SimulatedEntry;

@ExtendWith(MockitoExtension.class)
public class RecurrenceServiceImplTest {

    @InjectMocks
    private RecurrenceServiceImpl recurrenceService;

    @Mock
    private RecurrenceRepository recurrenceRepository;

    private static final LocalDateTime FROM = LocalDateTime.of(2025, 1, 1, 0, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2025, 1, 31, 23, 59, 59);
    private static final Currency CHF = new Currency("CHF", true);

    private Recurrence buildRecurrence(LocalDateTime startDate, Frequency frequency) {
        return Recurrence.builder()
                .title("Loyer")
                .description("Loyer mensuel")
                .amount(new BigDecimal("1200.00"))
                .currency(CHF)
                .startDate(startDate)
                .frequency(frequency)
                .build();
    }

    @Test
    void shouldReturnEmptyListWhenNoRecurrences() {
        when(recurrenceRepository.findAll()).thenReturn(List.of());

        List<SimulatedEntry> result = recurrenceService.simulateEntries(FROM, TO);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldExcludeRecurrencesWithFrequencyNone() {
        Recurrence recurrence = buildRecurrence(LocalDateTime.of(2025, 1, 15, 0, 0), Frequency.NONE);
        when(recurrenceRepository.findAll()).thenReturn(List.of(recurrence));

        List<SimulatedEntry> result = recurrenceService.simulateEntries(FROM, TO);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEntryWhenStartDateIsInInterval() {
        /*
        Recurrence starts on 2025-01-15 (within the interval Jan 1–31)
        Frequency MONTHLY → first occurrence is directly in the interval
        Expected: 1 simulated entry on 2025-01-15
        */
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 15, 0, 0);
        Recurrence recurrence = buildRecurrence(startDate, Frequency.MONTHLY);
        when(recurrenceRepository.findAll()).thenReturn(List.of(recurrence));

        List<SimulatedEntry> result = recurrenceService.simulateEntries(FROM, TO);

        assertEquals(1, result.size());
        assertEquals(startDate, result.get(0).accountingDate());
        assertEquals("Loyer", result.get(0).title());
        assertEquals("CHF", result.get(0).currencyCode());
    }

    @Test
    void shouldReturnEntryWhenNextOccurrenceIsInInterval() {
        /*
        Recurrence starts on 2024-12-15 (before the interval)
        Frequency MONTHLY → next occurrence is 2025-01-15 (within Jan 1–31)
        Expected: 1 simulated entry on 2025-01-15
        */
        LocalDateTime startDate = LocalDateTime.of(2024, 12, 15, 0, 0);
        Recurrence recurrence = buildRecurrence(startDate, Frequency.MONTHLY);
        when(recurrenceRepository.findAll()).thenReturn(List.of(recurrence));

        List<SimulatedEntry> result = recurrenceService.simulateEntries(FROM, TO);

        assertEquals(1, result.size());
        assertEquals(LocalDateTime.of(2025, 1, 15, 0, 0), result.get(0).accountingDate());
    }

    @Test
    void shouldReturnEmptyWhenRecurrenceStartsAfterInterval() {
        /*
        Recurrence starts on 2025-02-01 (after the interval Jan 1–31)
        Expected: no simulated entry
        */
        Recurrence recurrence = buildRecurrence(LocalDateTime.of(2025, 2, 1, 0, 0), Frequency.MONTHLY);
        when(recurrenceRepository.findAll()).thenReturn(List.of(recurrence));

        List<SimulatedEntry> result = recurrenceService.simulateEntries(FROM, TO);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnMultipleEntriesForMultipleRecurrences() {
        /*
        2 recurrences both with a monthly occurrence in Jan 2025
        Expected: 2 simulated entries
        */
        Recurrence r1 = buildRecurrence(LocalDateTime.of(2025, 1, 10, 0, 0), Frequency.MONTHLY);
        Recurrence r2 = buildRecurrence(LocalDateTime.of(2025, 1, 20, 0, 0), Frequency.MONTHLY);
        when(recurrenceRepository.findAll()).thenReturn(List.of(r1, r2));

        List<SimulatedEntry> result = recurrenceService.simulateEntries(FROM, TO);

        assertEquals(2, result.size());
    }

    @Test
    void shouldMapRecurrenceFieldsToSimulatedEntry() {
        Recurrence recurrence = Recurrence.builder()
                .title("Salaire")
                .description("Salaire mensuel")
                .amount(new BigDecimal("5000.00"))
                .currency(CHF)
                .startDate(LocalDateTime.of(2025, 1, 5, 0, 0))
                .frequency(Frequency.MONTHLY)
                .tags(Set.of())
                .build();
        when(recurrenceRepository.findAll()).thenReturn(List.of(recurrence));

        List<SimulatedEntry> result = recurrenceService.simulateEntries(FROM, TO);

        assertEquals(1, result.size());
        SimulatedEntry entry = result.get(0);
        assertEquals("Salaire", entry.title());
        assertEquals("Salaire mensuel", entry.description());
        assertEquals(new BigDecimal("5000.00"), entry.amount());
        assertEquals("CHF", entry.currencyCode());
        assertTrue(entry.tags().isEmpty());
    }
}
