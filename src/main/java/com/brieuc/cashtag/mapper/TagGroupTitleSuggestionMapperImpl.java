package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.TagGroupTitleSuggestionDto;
import com.brieuc.cashtag.entity.TagGroupTitleSuggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagGroupTitleSuggestionMapperImpl implements TagGroupTitleSuggestionMapper {

    @Override
    public TagGroupTitleSuggestionDto toDto(TagGroupTitleSuggestion entity) {
        return TagGroupTitleSuggestionDto.builder()
                .id(entity.getId())
                .tagGroupId(entity.getTagGroup().getId())
                .title(entity.getTitle())
                .usageCount(entity.getUsageCount())
                .lastUsed(entity.getLastUsed())
                .build();
    }
}
