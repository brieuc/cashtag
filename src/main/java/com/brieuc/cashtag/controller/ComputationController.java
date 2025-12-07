package com.brieuc.cashtag.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;
import com.brieuc.cashtag.service.ComputationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/computation", produces = "application/json")
@RequiredArgsConstructor
public class ComputationController {
      
      private final ComputationService computationService;

      @PostMapping(consumes = "application/json")
      public ResponseEntity<ComputationResponseDto> compute(@RequestBody ComputationRequestDto computationRequestDto) {
            ComputationResponseDto computationResponseDto = computationService.computeSum(computationRequestDto);
            return ResponseEntity.ok(computationResponseDto);
      }
}
