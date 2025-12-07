package com.brieuc.cashtag.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EntrySpecificationDto {
      
      // Need to use LocalDate instead of LocalDateTime because of query params
      // serialized by Spring MVC and not from Jackson : 2025-12-06T19:54:41.032Z
      // We will use startOfDay and endOfDay in the specification mapper.
      LocalDate startDate;
      LocalDate endDate;
      Set<Long> tagIds;
      Double startAmount;
      Double endAmount;
      Set<String> currencyCodes;
}
