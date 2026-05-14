package com.brieuc.cashtag.dto.calculation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of aggregated amount calculation")
public record ComputationResponseDto(
      @Schema(description = "Start date of the calculated period", example = "2024-01-01T00:00:00")
      LocalDateTime startDate,

      @Schema(description = "End date of the calculated period", example = "2024-12-31T23:59:59")
      LocalDateTime endDate,

      @Schema(description = "Total amount converted to the target currency", example = "15750.50")
      BigDecimal totalAmount,

      @Schema(description = "ISO 4217 code of the target currency used for conversion", example = "CHF")
      String targetCurrencyCode,

      @Schema(description = "Total number of entries included in the calculation", example = "42")
      Long numberOfEntries,

      @Schema(description = "Breakdown of calculations by source currency (key = currency code, value = calculation details)", example = "{\"EUR\": {\"currencyCode\": \"EUR\", \"numberOfEntries\": 15, \"totalAmount\": \"5000.00\"}, \"USD\": {\"currencyCode\": \"USD\", \"numberOfEntries\": 10, \"totalAmount\": \"3000.00\"}}")
      Map<String, ComputationCurrencyAmountDto> computationByCurrency) {

}
