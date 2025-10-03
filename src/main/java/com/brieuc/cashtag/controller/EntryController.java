package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.EntryDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.service.CurrencyService;
import com.brieuc.cashtag.service.EntryService;
import com.brieuc.cashtag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/entries", produces = "application/json", consumes = "application/json")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;
    private final CurrencyService currencyService;
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<EntryDto>> getAllEntries() {
        List<EntryDto> entries = entryService.findAll().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntryDto> getEntryById(@PathVariable Long id) {
        return entryService.findById(id)
                .map(e -> ResponseEntity.ok(toDto(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<EntryDto>> getEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<EntryDto> entries = entryService.findByDateRange(startDate, endDate).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/by-tag/{tagId}")
    public ResponseEntity<List<EntryDto>> getEntriesByTag(@PathVariable Long tagId) {
        List<EntryDto> entries = entryService.findByTag(tagId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/by-currency/{currencyCode}")
    public ResponseEntity<List<EntryDto>> getEntriesByCurrency(@PathVariable String currencyCode) {
        List<EntryDto> entries = entryService.findByCurrency(currencyCode).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(entries);
    }

    @PostMapping
    public ResponseEntity<EntryDto> createEntry(@RequestBody EntryDto dto) {
        Currency currency = currencyService.findByCode(dto.getCurrencyCode())
                .orElseThrow(() -> new IllegalArgumentException("Currency not found"));

        Set<Tag> tags = dto.getTagTitles().stream()
                .map(title -> tagService.findByTitle(title)
                        .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + title)))
                .collect(Collectors.toSet());

        Entry entry = Entry.builder()
                .accountingDate(dto.getAccountingDate())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .amount(dto.getAmount())
                .currency(currency)
                .tags(tags)
                .build();
        Entry saved = entryService.save(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntryDto> updateEntry(@PathVariable Long id, @RequestBody EntryDto dto) {
        return entryService.findById(id)
                .map(existing -> {
                    Currency currency = currencyService.findByCode(dto.getCurrencyCode())
                            .orElseThrow(() -> new IllegalArgumentException("Currency not found"));

                    Set<Tag> tags = dto.getTagTitles().stream()
                            .map(title -> tagService.findByTitle(title)
                                    .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + title)))
                            .collect(Collectors.toSet());

                    existing.setAccountingDate(dto.getAccountingDate());
                    existing.setTitle(dto.getTitle());
                    existing.setDescription(dto.getDescription());
                    existing.setAmount(dto.getAmount());
                    existing.setCurrency(currency);
                    existing.setTags(tags);

                    Entry updated = entryService.save(existing);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        if (entryService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        entryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private EntryDto toDto(Entry entry) {
        Set<String> tagTitles = entry.getTags().stream()
                .map(Tag::getTitle)
                .collect(Collectors.toSet());
        return new EntryDto(entry.getId(), entry.getAccountingDate(),
                          entry.getModificationDate(), entry.getTitle(),
                          entry.getDescription(), entry.getAmount(),
                          entry.getCurrency().getCode(), tagTitles);
    }
}