package com.brieuc.cashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryDto {
    private Long id;
    private LocalDateTime accountingDate;
    private LocalDateTime modificationDate;
    private String title;
    private String description;
    private BigDecimal amount;
    private String currencyCode;
    private Set<TagDto> tags;
}