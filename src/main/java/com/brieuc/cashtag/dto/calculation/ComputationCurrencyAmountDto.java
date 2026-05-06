package com.brieuc.cashtag.dto.calculation;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Breakdown of aggregated calculation for a specific currency")
public record ComputationCurrencyAmountDto(
      @Schema(description = "ISO 4217 currency code", example = "EUR")
      String currencyCode,

      @Schema(description = "Number of entries in this currency", example = "15")
      Long numberOfEntries,

      @Schema(description = "Total amount in this currency (before conversion)", example = "5000.00")
      BigDecimal totalAmount) {

}