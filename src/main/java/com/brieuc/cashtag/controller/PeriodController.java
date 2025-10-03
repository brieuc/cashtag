package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.PeriodDto;
import com.brieuc.cashtag.entity.Period;
import com.brieuc.cashtag.service.PeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/periods", produces = "application/json", consumes = "application/json")
@RequiredArgsConstructor
public class PeriodController {

    private final PeriodService periodService;

    @GetMapping
    public ResponseEntity<List<PeriodDto>> getAllPeriods() {
        List<PeriodDto> periods = periodService.findAll().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(periods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodDto> getPeriodById(@PathVariable Long id) {
        return periodService.findById(id)
                .map(p -> ResponseEntity.ok(toDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-date")
    public ResponseEntity<PeriodDto> getPeriodByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return periodService.findPeriodByDate(date)
                .map(p -> ResponseEntity.ok(toDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PeriodDto> createPeriod(@RequestBody PeriodDto dto) {
        Period period = new Period(null, dto.getTitle(), dto.getStartDate(), dto.getEndDate());
        Period saved = periodService.save(period);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeriodDto> updatePeriod(@PathVariable Long id, @RequestBody PeriodDto dto) {
        return periodService.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.getTitle());
                    existing.setStartDate(dto.getStartDate());
                    existing.setEndDate(dto.getEndDate());
                    Period updated = periodService.save(existing);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePeriod(@PathVariable Long id) {
        if (periodService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        periodService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private PeriodDto toDto(Period period) {
        return new PeriodDto(period.getId(), period.getTitle(),
                           period.getStartDate(), period.getEndDate());
    }
}