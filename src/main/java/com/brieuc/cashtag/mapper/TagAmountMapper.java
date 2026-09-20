package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.calculation.TagAmountDto;
import com.brieuc.cashtag.service.helper.TagAmount;

@Service
public interface TagAmountMapper {

      TagAmountDto toDto(TagAmount tagAmount);
}
