package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.PeriodDto;
import com.brieuc.cashtag.entity.Period;

@Service
public class PeriodMapperImpl implements PeriodMapper {

      @Override
      public Period toEntity(PeriodDto periodDto) {
            Period period = Period.builder()
                  .id(periodDto.getId())
                  .title(periodDto.getTitle())
                  .startDate(periodDto.getStartDate())
                  .endDate(periodDto.getEndDate())
                  .build();
            return period;
      }

      @Override
      public PeriodDto toDto(Period period) {
            PeriodDto periodDto = PeriodDto.builder()
                  .id(period.getId())
                  .title(period.getTitle())
                  .startDate(period.getStartDate())
                  .endDate(period.getEndDate())
                  .build();
            return periodDto;
      }
}