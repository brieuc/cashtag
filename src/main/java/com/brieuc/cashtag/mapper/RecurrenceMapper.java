package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.RecurrenceDto;
import com.brieuc.cashtag.entity.Recurrence;
import org.springframework.stereotype.Service;

@Service
public interface RecurrenceMapper {
    Recurrence toEntity(RecurrenceDto recurrenceDto);
    RecurrenceDto toDto(Recurrence recurrence);
}
