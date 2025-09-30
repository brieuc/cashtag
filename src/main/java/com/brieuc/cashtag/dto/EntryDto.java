package com.brieuc.cashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String currencyCode;
    private Set<Long> tagIds;
}