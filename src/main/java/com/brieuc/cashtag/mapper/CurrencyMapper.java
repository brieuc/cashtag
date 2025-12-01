package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.CurrencyDto;
import com.brieuc.cashtag.entity.Currency;

@Service
public interface CurrencyMapper {

      Currency toEntity(CurrencyDto currencyDto);
      CurrencyDto toDto(Currency currency);
}