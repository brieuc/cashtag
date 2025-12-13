package com.brieuc.cashtag.dto.calculation;

import java.time.LocalDate;
import java.util.Set;

import com.brieuc.cashtag.dto.TagDto;


public record ComputationRequestDto(
      // Use in entrySpecificationDto so we're staying with LocalDate
      // instead of LocalDateTime, the time will be add in the specification building
      // like in the get for entries.
      LocalDate startDate,
      LocalDate endDate,
      Set<TagDto> tags,
      // Target currency code : either reference currency or a tag currency
      String targetCurrencyCode
) {

}