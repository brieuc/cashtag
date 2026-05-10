package com.brieuc.cashtag.dto;

import com.brieuc.cashtag.entity.Frequency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
@Schema(description = "Filter criteria for searching recurrences")
public class RecurrenceSpecificationDto {

    @Schema(description = "Start date filter (inclusive)", example = "2024-01-01T00:00:00")
    LocalDateTime startDate;

    @Schema(description = "End date filter (inclusive)", example = "2026-12-31T23:59:59")
    LocalDateTime endDate;

    @Schema(description = "List of tag IDs to filter recurrences", example = "[1, 2, 5]")
    Set<Long> tagIds;

    @Schema(description = "Frequencies to filter recurrences", example = "[\"MONTHLY\", \"YEARLY\"]")
    Set<Frequency> frequencies;
}
