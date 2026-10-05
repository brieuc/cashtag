package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.calculation.TagsAmountDto;
import com.brieuc.cashtag.service.helper.TagsAmount;

@Service
public interface TagAmountMapper {

      TagsAmountDto toDto(TagsAmount tagsAmount);
}
