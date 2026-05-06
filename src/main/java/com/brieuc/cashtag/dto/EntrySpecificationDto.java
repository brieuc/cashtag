package com.brieuc.cashtag.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "Filter criteria for searching financial entries")
public class EntrySpecificationDto {

      @Schema(description = "Start date of the search period (inclusive, will use start of day)", example = "2024-01-01")
      LocalDate startDate;

      @Schema(description = "End date of the search period (inclusive, will use end of day)", example = "2026-12-31")
      LocalDate endDate;

      @Schema(description = "List of tag IDs to filter entries", example = "[1, 2, 5]")
      Set<Long> tagIds;

      @Schema(description = "Minimum transaction amount", example = "100.0")
      Double startAmount;

      @Schema(description = "Maximum transaction amount", example = "5000.0")
      Double endAmount;

      @Schema(description = "Currency codes to filter entries (ISO 4217 codes)", example = "[\"CHF\", \"EUR\", \"USD\"]")
      Set<String> currencyCodes;
}
