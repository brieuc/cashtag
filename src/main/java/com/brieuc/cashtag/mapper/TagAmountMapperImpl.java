package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.calculation.TagsAmountDto;
import com.brieuc.cashtag.service.helper.TagsAmount;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagAmountMapperImpl implements TagAmountMapper {

      private final TagMapper tagMapper;

      @Override
      public TagsAmountDto toDto(TagsAmount tagsAmount) {
            return new TagsAmountDto(
                  tagsAmount.tags().stream().map(tagMapper::toDto).toList(),
                  tagsAmount.amount());
      }
}
