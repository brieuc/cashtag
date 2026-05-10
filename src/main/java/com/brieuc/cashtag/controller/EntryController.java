package com.brieuc.cashtag.controller;

import java.util.List;

import com.brieuc.cashtag.controller.api.EntryApi;
import com.brieuc.cashtag.dto.EntryDto;
import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.mapper.EntryMapper;
import com.brieuc.cashtag.mapper.EntrySpecificationMapper;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EntryController implements EntryApi {

    private final EntryService entryService;
    private final PageRequestMapper pageRequestMapper;
    private final EntryMapper entryMapper;
    private final EntrySpecificationMapper entrySpecificationMapper;

    @Override
    public ResponseEntity<PageImpl<EntryDto>> getEntries(
            @ParameterObject EntrySpecificationDto entrySpecificationDto,
            @ParameterObject PageRequestDto pageRequestDto) {
        Specification<Entry> specification = entrySpecificationMapper.toEntity(entrySpecificationDto);
        Page<EntryDto> entries = entryService.getEntries(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(entryMapper::tDto);
        return ResponseEntity.ok(new PageImpl<>(entries.getContent(), entries.getPageable(), entries.getTotalElements()));
    }

    @Override
    public ResponseEntity<EntryDto> getEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(entryMapper.tDto(entryService.getById(id)));
    }

    @Override
    public ResponseEntity<List<EntryDto>> createBatch(@RequestBody List<EntryDto> entryDtos) {
        List<Entry> entries = entryDtos.stream().map(entryMapper::toEntity).toList();
        List<EntryDto> created = entryService.createBatch(entries).stream().map(entryMapper::tDto).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<EntryDto> createEntry(@RequestBody EntryDto entryDto) {
        Entry newEntry = entryService.create(entryMapper.toEntity(entryDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(entryMapper.tDto(newEntry));
    }

    @Override
    public ResponseEntity<EntryDto> updateEntry(@PathVariable Long id, @RequestBody EntryDto entryDto) {
        Entry updatedEntry = entryService.update(entryMapper.toEntity(entryDto));
        return ResponseEntity.ok(entryMapper.tDto(updatedEntry));
    }

    @Override
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        entryService.delete(entryService.getById(id));
        return ResponseEntity.noContent().build();
    }
}
