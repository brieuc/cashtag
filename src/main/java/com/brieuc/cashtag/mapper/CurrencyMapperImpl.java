package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.CurrencyDto;
import com.brieuc.cashtag.entity.Currency;

@Service
public class CurrencyMapperImpl implements CurrencyMapper {

      @Override
      public Currency toEntity(CurrencyDto currencyDto) {
            Currency currency = Currency.builder()
                  .code(currencyDto.getCode())
                  .build();
            return currency;
      }

      @Override
      public CurrencyDto toDto(Currency currency) {
            CurrencyDto currencyDto = CurrencyDto.builder()
                  .code(currency.getCode())
                  .build();
            return currencyDto;
      }
}