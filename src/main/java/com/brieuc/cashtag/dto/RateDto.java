package com.brieuc.cashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {
    private Long id;
    private String currencyCode;
    private LocalDate valueDate;
    private BigDecimal ratePercent;
}