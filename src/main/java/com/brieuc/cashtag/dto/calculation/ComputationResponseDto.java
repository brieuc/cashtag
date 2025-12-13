package com.brieuc.cashtag.dto.calculation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;


public record ComputationResponseDto(
      LocalDate startDate,
      LocalDate endDate,
      BigDecimal totalAmount,
      String targetCurrencyCode,
      Long numberOfEntries,
      Map<String, ComputationCurrencyAmountDto> computationByCurrency) {

}
