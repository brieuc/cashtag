package com.brieuc.cashtag.dto;

import java.time.LocalDateTime;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "Filter criteria for searching financial entries")
public class EntrySpecificationDto {

      @Schema(description = "Start date of the search period (inclusive)", example = "2024-01-01T00:00:00")
      LocalDateTime startDate;

      @Schema(description = "End date of the search period (inclusive)", example = "2026-12-31T23:59:59")
      LocalDateTime endDate;

      @Schema(description = "List of tag IDs to filter entries", example = "[1, 2, 5]")
      Set<Long> tagIds;

      @Schema(description = "Minimum transaction amount", example = "100.0")
      Double startAmount;

      @Schema(description = "Maximum transaction amount", example = "5000.0")
      Double endAmount;

      @Schema(description = "Currency codes to filter entries (ISO 4217 codes)", example = "[\"CHF\", \"EUR\", \"USD\"]")
      Set<String> currencyCodes;

      @Schema(description = "Text search on title and description (case-insensitive, partial match)", example = "laptop")
      String searchText;
}
