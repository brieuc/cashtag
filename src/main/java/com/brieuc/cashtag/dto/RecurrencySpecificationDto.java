package com.brieuc.cashtag.dto;

import com.brieuc.cashtag.entity.Frequency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
@Schema(description = "Filter criteria for searching recurrencies")
public class RecurrencySpecificationDto {

    @Schema(
            description = "Start date filter (inclusive)",
            example = "2024-01-01"
    )
    LocalDate startDate;

    @Schema(
            description = "End date filter (inclusive)",
            example = "2026-12-31"
    )
    LocalDate endDate;

    @Schema(
            description = "List of tag IDs to filter recurrencies",
            example = "[1, 2, 5]"
    )
    Set<Long> tagIds;

    @Schema(
            description = "Frequencies to filter recurrencies",
            example = "[\"MONTHLY\", \"YEARLY\"]"
    )
    Set<Frequency> frequencies;
}
