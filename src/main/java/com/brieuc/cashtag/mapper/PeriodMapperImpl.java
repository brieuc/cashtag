package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.PeriodDto;
import com.brieuc.cashtag.entity.Period;
import com.brieuc.cashtag.service.CurrencyService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PeriodMapperImpl implements PeriodMapper {

      private final CurrencyService currencyService;
      private final CurrencyMapper currencyMapper;

      @Override
      public Period toEntity(PeriodDto periodDto) {
            Period period = Period.builder()
                  .id(periodDto.getId())
                  .title(periodDto.getTitle())
                  .startDate(periodDto.getStartDate())
                  .endDate(periodDto.getEndDate())
                  .currency(periodDto.getCurrency() != null ? currencyService.getCurrencyByCode(periodDto.getCurrency().getCode()) : null)
                  .hidden(periodDto.getHidden())
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
                  .currency(period.getCurrency() != null ? currencyMapper.toDto(period.getCurrency()) : null)
                  .hidden(period.getHidden())
                  .build();
            return periodDto;
      }
}