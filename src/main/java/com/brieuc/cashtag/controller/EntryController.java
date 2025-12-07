package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.EntryDto;
import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.mapper.EntryMapper;
import com.brieuc.cashtag.mapper.EntrySpecificationMapper;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.TagMapper;
import com.brieuc.cashtag.service.CurrencyService;
import com.brieuc.cashtag.service.EntryService;
import com.brieuc.cashtag.service.EntryServiceImpl;
import com.brieuc.cashtag.service.TagService;
import com.brieuc.cashtag.service.TagServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/entries", produces = "application/json")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;
    private final PageRequestMapper pageRequestMapper;
    private final EntryMapper entryMapper;
    private final EntrySpecificationMapper entrySpecificationMapper;

    @GetMapping
    public ResponseEntity<Page<EntryDto>> getAllEntries(@ModelAttribute EntrySpecificationDto entrySpecificationDto,
                                                        @ModelAttribute PageRequestDto pageRequestDto) {
        Specification<Entry> specification = entrySpecificationMapper.toEntity(entrySpecificationDto);
        Page<EntryDto> entries = entryService.getEntries(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(entryMapper::tDto);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntryDto> getEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(entryMapper.tDto(entryService.getById(id)));

        /*
        return entryService.findById(id)
                .map(e -> ResponseEntity.ok(toDto(e)))
                .orElse(ResponseEntity.notFound().build());
        */
    }
/*
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
        List<EntryDto> entries = entryService.getByCurrency(currencyCode).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(entries);
    }
         */

    @PostMapping(consumes = "application/json")
    public ResponseEntity<EntryDto> createEntry(@RequestBody EntryDto entryDto) {
        Entry newEntry = entryService.create(entryMapper.toEntity(entryDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(entryMapper.tDto(newEntry));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<EntryDto> updateEntry(@PathVariable Long id, @RequestBody EntryDto entryDto) {
        
        if(!id.equals(entryDto.getId()))
                throw new RuntimeException("no tag corresponding to this id");
         
        Entry updatedEntry = entryService.update(entryMapper.toEntity(entryDto));
        return ResponseEntity.status(HttpStatus.OK).body(entryMapper.tDto(updatedEntry));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        Entry entry = entryService.getById(id);
        entryService.delete(entry);
        return ResponseEntity.noContent().build();
    }

}