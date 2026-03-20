package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.RecurrenceSpecificationDto;
import com.brieuc.cashtag.entity.Recurrence;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface RecurrenceSpecificationMapper {
    Specification<Recurrence> toEntity(RecurrenceSpecificationDto recurrenceSpecificationDto);
}
