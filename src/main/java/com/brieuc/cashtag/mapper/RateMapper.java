package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.RateDto;
import com.brieuc.cashtag.entity.Rate;

@Service
public interface RateMapper {

    Rate toEntity(RateDto rateDto);
    RateDto toDto(Rate rate);
}
