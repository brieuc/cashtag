package com.brieuc.cashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryDto {
    private Long id;
    private LocalDate accountingDate;
    private LocalDateTime modificationDate;
    private String title;
    private String description;
    private BigDecimal amount;
    private String currencyCode;
    private Set<String> tagTitles;
}