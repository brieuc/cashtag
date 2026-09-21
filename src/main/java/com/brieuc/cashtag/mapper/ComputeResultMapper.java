package com.brieuc.cashtag.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;
import com.brieuc.cashtag.service.helper.ComputeResult;

@Service
public interface ComputeResultMapper {

      ComputationResponseDto toDto(ComputeResult computeResult, LocalDateTime startDate, LocalDateTime endDate);
}
