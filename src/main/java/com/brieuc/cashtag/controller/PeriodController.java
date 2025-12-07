package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.PeriodDto;
import com.brieuc.cashtag.entity.Period;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.PeriodMapper;
import com.brieuc.cashtag.service.PeriodServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/periods", produces = "application/json")
@RequiredArgsConstructor
public class PeriodController {

    private final PeriodServiceImpl periodService;
    private final PeriodMapper periodMapper;
    private final PageRequestMapper pageRequestMapper;

    @GetMapping
    public ResponseEntity<Page<PeriodDto>> getAllPeriods(@ModelAttribute PageRequestDto pageRequestDto) {

        Specification<Period> specification = Specification.unrestricted();
        Page<PeriodDto> periods = periodService.getPeriods(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(periodMapper::toDto);

        return ResponseEntity.ok(periods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodDto> getPeriodById(@PathVariable Long id) {
        return ResponseEntity.ok(periodMapper.toDto(periodService.getById(id)));
    }

    @GetMapping("/by-date")
    public ResponseEntity<PeriodDto> getPeriodByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return periodService.findPeriodByDate(date)
                .map(p -> ResponseEntity.ok(periodMapper.toDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<PeriodDto> createPeriod(@RequestBody PeriodDto periodDto) {
        Period saved = periodService.create(periodMapper.toEntity(periodDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(periodMapper.toDto(saved));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<PeriodDto> updatePeriod(@PathVariable Long id, @RequestBody PeriodDto periodDto) {
        if (!periodDto.getId().equals(id))
            throw new RuntimeException("no period corresponding to this id");
        Period period = periodService.update(periodMapper.toEntity(periodDto));
        return ResponseEntity.ok(periodMapper.toDto(period));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePeriod(@PathVariable Long id) {
        periodService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}