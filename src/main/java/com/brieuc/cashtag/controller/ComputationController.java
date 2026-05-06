package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.controller.api.ComputationApi;
import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;
import com.brieuc.cashtag.service.ComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ComputationController implements ComputationApi {

    private final ComputationService computationService;

    @Override
    public ResponseEntity<ComputationResponseDto> compute(@RequestBody ComputationRequestDto computationRequestDto) {
        return ResponseEntity.ok(computationService.computeSum(computationRequestDto));
    }
}
