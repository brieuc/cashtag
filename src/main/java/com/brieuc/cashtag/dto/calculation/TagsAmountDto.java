package com.brieuc.cashtag.dto.calculation;

import java.math.BigDecimal;
import java.util.List;

import com.brieuc.cashtag.dto.TagDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aggregated amount for a specific tag")
public record TagsAmountDto(
      @Schema(description = "Tag the amount is aggregated for")
      List<TagDto> tags,

      @Schema(description = "Total amount aggregated for this tag", example = "1500.00")
      BigDecimal amount) {

}
