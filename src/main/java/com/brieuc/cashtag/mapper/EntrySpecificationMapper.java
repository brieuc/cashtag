package com.brieuc.cashtag.mapper;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.entity.Entry;

@Service
public interface EntrySpecificationMapper {
      Specification<Entry> toEntity(EntrySpecificationDto entrySpecificationDto);
}
