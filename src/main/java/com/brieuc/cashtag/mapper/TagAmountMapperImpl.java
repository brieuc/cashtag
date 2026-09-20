package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.calculation.TagAmountDto;
import com.brieuc.cashtag.service.helper.TagAmount;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagAmountMapperImpl implements TagAmountMapper {

      private final TagMapper tagMapper;

      @Override
      public TagAmountDto toDto(TagAmount tagAmount) {
            return new TagAmountDto(
                  tagMapper.toDto(tagAmount.tag()),
                  tagAmount.amount());
      }
}
