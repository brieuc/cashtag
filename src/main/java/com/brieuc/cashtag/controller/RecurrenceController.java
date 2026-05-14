package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.controller.api.RecurrenceApi;
import com.brieuc.cashtag.dto.EntryDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.RecurrenceDto;
import com.brieuc.cashtag.dto.RecurrenceSpecificationDto;
import com.brieuc.cashtag.entity.Recurrence;
import com.brieuc.cashtag.mapper.EntryMapper;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.RecurrenceMapper;
import com.brieuc.cashtag.mapper.RecurrenceSpecificationMapper;
import com.brieuc.cashtag.service.RecurrenceService;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecurrenceController implements RecurrenceApi {

    private final RecurrenceService recurrenceService;
    private final PageRequestMapper pageRequestMapper;
    private final RecurrenceMapper recurrenceMapper;
    private final RecurrenceSpecificationMapper recurrenceSpecificationMapper;
    private final EntryMapper entryMapper;

    @Override
    public ResponseEntity<PageImpl<RecurrenceDto>> getRecurrences(
            @ParameterObject RecurrenceSpecificationDto recurrenceSpecificationDto,
            @ParameterObject PageRequestDto pageRequestDto) {
        Specification<Recurrence> specification = recurrenceSpecificationMapper.toEntity(recurrenceSpecificationDto);
        Page<RecurrenceDto> recurrences = recurrenceService
                .getRecurrences(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(recurrenceMapper::toDto);
        return ResponseEntity.ok(
                new PageImpl<>(recurrences.getContent(), recurrences.getPageable(), recurrences.getTotalElements()));
    }

    @Override
    public ResponseEntity<List<EntryDto>> simulateEntries(
            @RequestParam LocalDateTime fromDate,
            @RequestParam LocalDateTime toDate) {
        List<EntryDto> result = recurrenceService.simulateEntries(fromDate, toDate).stream()
                .map(entryMapper::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<RecurrenceDto> getRecurrenceById(@PathVariable Long id) {
        return ResponseEntity.ok(recurrenceMapper.toDto(recurrenceService.getById(id)));
    }

    @Override
    public ResponseEntity<RecurrenceDto> createRecurrence(@RequestBody RecurrenceDto recurrenceDto) {
        Recurrence newRecurrence = recurrenceService.create(recurrenceMapper.toEntity(recurrenceDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(recurrenceMapper.toDto(newRecurrence));
    }

    @Override
    public ResponseEntity<RecurrenceDto> updateRecurrence(@PathVariable Long id,
            @RequestBody RecurrenceDto recurrenceDto) {
        Recurrence updatedRecurrence = recurrenceService.update(recurrenceMapper.toEntity(recurrenceDto));
        return ResponseEntity.ok(recurrenceMapper.toDto(updatedRecurrence));
    }

    @Override
    public ResponseEntity<Void> deleteRecurrence(@PathVariable Long id) {
        recurrenceService.delete(recurrenceService.getById(id));
        return ResponseEntity.noContent().build();
    }
}
