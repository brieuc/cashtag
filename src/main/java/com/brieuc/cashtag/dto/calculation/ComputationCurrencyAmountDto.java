package com.brieuc.cashtag.dto.calculation;

import java.math.BigDecimal;


public record ComputationCurrencyAmountDto(
      String currencyCode,
      Long numberOfEntries,
      BigDecimal totalAmount) {
      
}