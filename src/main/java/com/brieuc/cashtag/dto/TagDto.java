package com.brieuc.cashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {
    private Long id;
    private String title;
    private String description;
    private String icon;
}