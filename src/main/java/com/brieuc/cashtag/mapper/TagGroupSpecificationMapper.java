package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.TagGroupSpecificationDto;
import com.brieuc.cashtag.entity.TagGroup;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface TagGroupSpecificationMapper {
    Specification<TagGroup> toEntity(TagGroupSpecificationDto tagGroupSpecificationDto);
}
