package com.brieuc.cashtag.dto;

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
public class RateDto {
    private Long id;
    private String sourceCurrencyCode;
    // Target currency is the reference currency
    private String targetCurrencyCode;
    private LocalDate valueDate;
    private BigDecimal rate;
}