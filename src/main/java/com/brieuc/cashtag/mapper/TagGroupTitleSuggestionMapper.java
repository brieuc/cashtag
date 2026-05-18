package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.TagGroupTitleSuggestionDto;
import com.brieuc.cashtag.entity.TagGroupTitleSuggestion;
import org.springframework.stereotype.Service;

@Service
public interface TagGroupTitleSuggestionMapper {
    TagGroupTitleSuggestionDto toDto(TagGroupTitleSuggestion entity);
}
