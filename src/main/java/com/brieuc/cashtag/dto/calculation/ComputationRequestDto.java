package com.brieuc.cashtag.dto.calculation;

import java.time.LocalDateTime;
import java.util.Set;

import com.brieuc.cashtag.dto.TagDto;


public record ComputationRequestDto(
      LocalDateTime startDate,
      LocalDateTime endDate,
      Set<TagDto> tags
) {

}