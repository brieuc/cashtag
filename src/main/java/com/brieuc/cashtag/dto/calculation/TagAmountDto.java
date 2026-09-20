package com.brieuc.cashtag.dto.calculation;

import java.math.BigDecimal;

import com.brieuc.cashtag.dto.TagDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aggregated amount for a specific tag")
public record TagAmountDto(
      @Schema(description = "Tag the amount is aggregated for")
      TagDto tag,

      @Schema(description = "Total amount aggregated for this tag", example = "1500.00")
      BigDecimal amount) {

}
