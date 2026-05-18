package com.brieuc.cashtag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
@Schema(description = "Filter criteria for searching tag groups")
public class TagGroupSpecificationDto {

    @Schema(description = "List of tag IDs to filter tag groups", example = "[1, 2, 5]")
    Set<Long> tagIds;
}
