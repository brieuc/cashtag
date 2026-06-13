package com.brieuc.cashtag.dto.calculation;

import java.time.LocalDateTime;
import java.util.Set;

import com.brieuc.cashtag.dto.CurrencyDto;
import com.brieuc.cashtag.dto.TagDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request for aggregated amount calculation over a period")
public record ComputationRequestDto(
      @Schema(description = "Start date of the calculation period", example = "2024-01-01T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
      LocalDateTime startDate,

      @Schema(description = "End date of the calculation period", example = "2024-12-31T23:59:59", requiredMode = Schema.RequiredMode.REQUIRED)
      LocalDateTime endDate,

      @Schema(description = "List of tags to filter entries in the calculation", example = "[{\"id\": 1, \"title\": \"Office\"}]")
      Set<TagDto> tags,

      @Schema(description = "list of currencies to filter entries in the calculation")
      Set<CurrencyDto> currencies,

      @Schema(description = "Search text")
      String searchText,

      @Schema(description = "ISO 4217 code of the target currency for conversion (reference currency or tag currency)", example = "CHF", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 3, maxLength = 3)
      String targetCurrencyCode
) {

}