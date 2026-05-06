package com.brieuc.cashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a financial entry (transaction)")
public class EntryDto {
    @Schema(description = "Unique identifier of the entry", example = "1")
    private Long id;

    @Schema(description = "Accounting date of the transaction", example = "2024-01-15T10:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime accountingDate;

    @Schema(description = "Last modification date (managed automatically)", accessMode = Schema.AccessMode.READ_ONLY, example = "2024-01-15T10:30:00")
    private LocalDateTime modificationDate;

    @Schema(description = "Title or label of the entry", example = "Computer hardware purchase", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
    private String title;

    @Schema(description = "Detailed description of the transaction", example = "Purchase of 2 laptops for the development team", maxLength = 1000)
    private String description;

    @Schema(description = "Transaction amount (positive for credit, negative for debit)", example = "2500.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "ISO 4217 currency code (3 letters)", example = "CHF", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 3, maxLength = 3)
    private String currencyCode;

    @Schema(description = "List of tags/labels associated with this entry", example = "[{\"id\": 1, \"name\": \"Office\"}, {\"id\": 2, \"name\": \"IT\"}]")
    private List<TagDto> tags;
}