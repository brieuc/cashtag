package com.brieuc.cashtag.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Pagination parameters for list queries")
public class PageRequestDto {
    @Schema(
            description = "Page number (starts at 0)",
            example = "0",
            defaultValue = "0"
    )
    @Min(0)
    @Max(Integer.MAX_VALUE)
    Integer page;

    @Schema(
            description = "Number of elements per page",
            example = "20",
            minimum = "1",
            maximum = "1000"
    )
    @Min(1)
    @Max(1000)
    Integer size;

    @Schema(
            description = "Sort criteria (format: 'property:direction' where direction = asc|desc)",
            example = "[\"accountingDate:desc\", \"title:asc\"]"
    )
    List<String> sort;
}