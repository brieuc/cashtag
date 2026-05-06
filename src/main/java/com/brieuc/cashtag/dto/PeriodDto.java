package com.brieuc.cashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a defined time period")
public class PeriodDto {
    @Schema(description = "Unique identifier of the period", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @Schema(description = "Title or name of the period", example = "First quarter 2024", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
    private String title;

    @Schema(description = "Start date and time of the period", example = "2024-01-01T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startDate;

    @Schema(description = "End date and time of the period", example = "2024-03-31T23:59:59", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endDate;

    @Schema(description = "ISO 4217 currency code (3 letters)", example = "CHF", minLength = 3, maxLength = 3)
    private String currencyCode;

    @Schema(description = "Whether the period is hidden from display", example = "false")
    private Boolean hidden;
}