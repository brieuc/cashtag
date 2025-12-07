package com.brieuc.cashtag.dto.calculation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;


public record ComputationResponseDto(
      LocalDateTime startDate,
      LocalDateTime endDate,
      BigDecimal totalAmount,
      Long numberOfEntries,
      Map<String, ComputationCurrencyAmountDto> computationByCurrency) {

}
