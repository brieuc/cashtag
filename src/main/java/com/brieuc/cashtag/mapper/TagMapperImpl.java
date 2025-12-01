package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Tag;

@Service
public class TagMapperImpl implements TagMapper {

      @Override
      public Tag toEntity(TagDto tagDto) {
            Tag tag = Tag.builder()
                  .id(tagDto.getId())
                  .description(tagDto.getDescription())
                  .icon(tagDto.getIcon())
                  .title(tagDto.getTitle())
                  .sortingOrder(tagDto.getSortingOrder())
                  .build();
            return tag;
      }

      @Override
      public TagDto toDto(Tag tag) {
            TagDto tagDto = TagDto.builder()
                  .id(tag.getId())
                  .title(tag.getTitle())
                  .description(tag.getDescription())
                  .icon(tag.getIcon())
                  .sortingOrder(tag.getSortingOrder())
                  .build();
            return tagDto;
      }      
}
