package com.brieuc.cashtag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents an exchange rate between two currencies")
public class RateDto {
    @Schema(
            description = "Unique identifier of the exchange rate",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "1"
    )
    private Long id;

    @Schema(
            description = "ISO 4217 code of the source currency",
            example = "EUR",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3,
            maxLength = 3
    )
    private String sourceCurrencyCode;

    @Schema(
            description = "ISO 4217 code of the target currency (reference currency)",
            example = "CHF",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3,
            maxLength = 3
    )
    private String targetCurrencyCode;

    @Schema(
            description = "Validity date of the exchange rate",
            example = "2024-01-15",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate valueDate;

    @Schema(
            description = "Exchange rate value (1 unit of source = rate units of target)",
            example = "0.95",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal rate;
}