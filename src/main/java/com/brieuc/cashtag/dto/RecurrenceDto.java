package com.brieuc.cashtag.dto;

import com.brieuc.cashtag.entity.Frequency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a recurring financial transaction")
public class RecurrenceDto {

    @Schema(description = "Unique identifier of the recurrence", example = "1")
    private Long id;

    @Schema(description = "Last modification date (managed automatically)", accessMode = Schema.AccessMode.READ_ONLY, example = "2024-01-15T10:30:00")
    private LocalDateTime modificationDate;

    @Schema(description = "Title or label of the recurrence", example = "Loyer", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
    private String title;

    @Schema(description = "Detailed description of the recurrence", example = "Loyer mensuel appartement", maxLength = 1000)
    private String description;

    @Schema(description = "Transaction amount (positive for credit, negative for debit)", example = "1200.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "ISO 4217 currency code (3 letters)", example = "CHF", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 3, maxLength = 3)
    private String currencyCode;

    @Schema(description = "Start date of the recurrence", example = "2024-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @Schema(description = "Recurrence frequency", example = "MONTHLY", requiredMode = Schema.RequiredMode.REQUIRED)
    private Frequency frequency;

    @Schema(description = "List of tags associated with this recurrence")
    private List<TagDto> tags;
}
