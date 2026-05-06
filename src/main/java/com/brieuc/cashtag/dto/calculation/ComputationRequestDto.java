package com.brieuc.cashtag.dto.calculation;

import java.time.LocalDate;
import java.util.Set;

import com.brieuc.cashtag.dto.TagDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request for aggregated amount calculation over a period")
public record ComputationRequestDto(
      @Schema(description = "Start date of the calculation period", example = "2024-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
      LocalDate startDate,

      @Schema(description = "End date of the calculation period", example = "2024-12-31", requiredMode = Schema.RequiredMode.REQUIRED)
      LocalDate endDate,

      @Schema(description = "List of tags to filter entries in the calculation", example = "[{\"id\": 1, \"title\": \"Office\"}]")
      Set<TagDto> tags,

      @Schema(description = "ISO 4217 code of the target currency for conversion (reference currency or tag currency)", example = "CHF", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 3, maxLength = 3)
      String targetCurrencyCode
) {

}