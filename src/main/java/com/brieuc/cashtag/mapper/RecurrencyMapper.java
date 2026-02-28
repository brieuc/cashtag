package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.RecurrencyDto;
import com.brieuc.cashtag.entity.Recurrency;
import org.springframework.stereotype.Service;

@Service
public interface RecurrencyMapper {
    Recurrency toEntity(RecurrencyDto recurrencyDto);
    RecurrencyDto toDto(Recurrency recurrency);
}
