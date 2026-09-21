package com.brieuc.cashtag.mapper;

import java.time.LocalDateTime;

import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;
import com.brieuc.cashtag.service.helper.ComputeResult;

import org.springframework.stereotype.Service;

@Service
public class ComputeResultMapperImpl implements ComputeResultMapper {

      @Override
      public ComputationResponseDto toDto(ComputeResult computeResult, LocalDateTime startDate, LocalDateTime endDate) {
            return new ComputationResponseDto(
                  startDate,
                  endDate,
                  computeResult.totalAmount(),
                  computeResult.targetCurrencyCode(),
                  computeResult.numberOfEntries(),
                  computeResult.computationByCurrency());
      }
}
