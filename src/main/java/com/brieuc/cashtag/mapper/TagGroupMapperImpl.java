package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.TagGroupDto;
import com.brieuc.cashtag.entity.TagGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagGroupMapperImpl implements TagGroupMapper {

    private final TagMapper tagMapper;

    @Override
    public TagGroupDto toDto(TagGroup entity) {
        return TagGroupDto.builder()
                .id(entity.getId())
                .usageCount(entity.getUsageCount())
                .lastUsed(entity.getLastUsed())
                .tags(entity.getTags() == null ? null : entity.getTags().stream()
                        .map(tagMapper::toDto)
                        .toList())
                .build();
    }
}
