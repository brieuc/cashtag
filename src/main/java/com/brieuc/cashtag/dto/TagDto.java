package com.brieuc.cashtag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a tag/label for categorizing entries")
public class TagDto {
    @Schema(
            description = "Unique identifier of the tag",
            //accessMode = Schema.AccessMode.READ_ONLY,
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Name/title of the tag",
            example = "Office",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    private String title;

    @Schema(
            description = "Detailed description of the tag",
            example = "Office-related expenses and supplies",
            maxLength = 500
    )
    private String description;

    @Schema(
            description = "Icon associated with the tag (emoji or code)",
            example = "🏢",
            maxLength = 50
    )
    private String icon;

    @Schema(
            description = "Sort order for display",
            example = "10",
            minimum = "0"
    )
    private Integer sortingOrder;
}