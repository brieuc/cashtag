package com.brieuc.cashtag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a suggested title for a tag group")
public class TagGroupTitleSuggestionDto {

    @Schema(description = "Unique identifier of the suggestion", example = "1")
    private Long id;

    @Schema(description = "Identifier of the associated tag group", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tagGroupId;

    @Schema(description = "Suggested title", example = "Groceries", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
    private String title;

    @Schema(description = "Number of times this suggestion has been used", example = "5")
    private Integer usageCount;

    @Schema(description = "Date and time this suggestion was last used", example = "2026-05-15T10:30:00")
    private LocalDateTime lastUsed;
}
