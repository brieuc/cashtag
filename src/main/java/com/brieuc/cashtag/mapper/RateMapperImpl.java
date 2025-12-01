package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.RateDto;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.service.CurrencyService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RateMapperImpl implements RateMapper {

    private final CurrencyService currencyService;

    @Override
    public Rate toEntity(RateDto rateDto) {
        Rate rate = Rate.builder()
            .id(rateDto.getId())
            .currency(currencyService.getCurrencyByCode(rateDto.getCurrencyCode()))
            .valueDate(rateDto.getValueDate())
            .rate(rateDto.getRate())
            .build();
        return rate;
    }

    @Override
    public RateDto toDto(Rate rate) {
        RateDto rateDto = RateDto.builder()
            .id(rate.getId())
            .currencyCode(rate.getCurrency().getCode())
            .valueDate(rate.getValueDate())
            .rate(rate.getRate())
            .build();
        return rateDto;
    }
}
