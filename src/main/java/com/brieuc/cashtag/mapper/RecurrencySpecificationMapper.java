package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.RecurrencySpecificationDto;
import com.brieuc.cashtag.entity.Recurrency;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface RecurrencySpecificationMapper {
    Specification<Recurrency> toEntity(RecurrencySpecificationDto recurrencySpecificationDto);
}
