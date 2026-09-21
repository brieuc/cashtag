package com.brieuc.cashtag.service.helper;

import java.math.BigDecimal;
import java.util.Map;

import com.brieuc.cashtag.dto.calculation.ComputationCurrencyAmountDto;

public record ComputeResult(
      BigDecimal totalAmount,
      String targetCurrencyCode,
      long numberOfEntries,
      Map<String, ComputationCurrencyAmountDto> computationByCurrency
) {

};
