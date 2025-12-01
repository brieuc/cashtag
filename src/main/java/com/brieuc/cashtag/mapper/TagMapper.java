package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Tag;

@Service
public interface TagMapper {
      
      Tag toEntity(TagDto tagDto);
      TagDto toDto(Tag tag);
}
