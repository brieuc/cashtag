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
            .sourceCurrency(currencyService.getCurrencyByCode(rateDto.getSourceCurrencyCode()))
            .targetCurrency(currencyService.getCurrencyByCode(rateDto.getTargetCurrencyCode()))
            .valueDate(rateDto.getValueDate())
            .rate(rateDto.getRate())
            .build();
        return rate;
    }

    @Override
    public RateDto toDto(Rate rate) {
        RateDto rateDto = RateDto.builder()
            .id(rate.getId())
            .sourceCurrencyCode(rate.getSourceCurrency().getCode())
            .targetCurrencyCode(rate.getTargetCurrency().getCode())
            .valueDate(rate.getValueDate())
            .rate(rate.getRate())
            .build();
        return rateDto;
    }
}
