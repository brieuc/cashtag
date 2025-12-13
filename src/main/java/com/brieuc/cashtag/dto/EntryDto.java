package com.brieuc.cashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryDto {
    @Schema(hidden = true)
    private Long id;
    private LocalDateTime accountingDate;
    private LocalDateTime modificationDate;
    // private LocalDateTime modificationDate; internally managed
    private String title;
    private String description;
    private BigDecimal amount;
    @Schema(example = "CHF")
    private String currencyCode;
    private Set<TagDto> tags;
}