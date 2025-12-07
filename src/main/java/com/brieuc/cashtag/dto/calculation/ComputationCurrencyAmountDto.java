package com.brieuc.cashtag.dto.calculation;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ComputationCurrencyAmountDto(
      String currencyCode,
      Long numberOfEntries,
      BigDecimal totalAmount) {
      
}