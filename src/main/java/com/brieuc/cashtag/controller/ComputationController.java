package com.brieuc.cashtag.controller;

import java.util.List;

import com.brieuc.cashtag.controller.api.ComputationApi;
import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.service.ComputationService;
import com.brieuc.cashtag.service.EntryService;
import com.brieuc.cashtag.service.helper.ComputationSpecBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ComputationController implements ComputationApi {

    private final ComputationService computationService;
    private final EntryService entryService;
    private final ComputationSpecBuilder computationSpecBuilder;

    @Override
    public ResponseEntity<ComputationResponseDto> compute(@RequestBody ComputationRequestDto computationRequestDto) {
        Specification<Entry> specification = computationSpecBuilder.from(computationRequestDto);
        List<Entry> entries = entryService.getEntries(specification, Pageable.unpaged()).getContent();
        return ResponseEntity.ok(computationService.computeSum(
                entries,
                computationRequestDto.targetCurrencyCode(),
                computationRequestDto.startDate(),
                computationRequestDto.endDate()));
    }
}
