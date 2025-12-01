package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.PeriodDto;
import com.brieuc.cashtag.entity.Period;

@Service
public interface PeriodMapper {

      Period toEntity(PeriodDto periodDto);
      PeriodDto toDto(Period period);
}