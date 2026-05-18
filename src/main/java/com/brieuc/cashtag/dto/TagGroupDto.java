package com.brieuc.cashtag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a group of tags frequently used together")
public class TagGroupDto {

    @Schema(description = "Unique identifier of the tag group", example = "1")
    private Long id;

    @Schema(description = "Number of times this tag group has been used", example = "12")
    private Integer usageCount;

    @Schema(description = "Date and time this tag group was last used", example = "2026-05-15T10:30:00")
    private LocalDateTime lastUsed;

    @Schema(description = "Tags belonging to this group")
    private List<TagDto> tags;

}
