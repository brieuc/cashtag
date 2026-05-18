package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.TagGroupDto;
import com.brieuc.cashtag.entity.TagGroup;
import org.springframework.stereotype.Service;

@Service
public interface TagGroupMapper {
    TagGroupDto toDto(TagGroup entity);
}
