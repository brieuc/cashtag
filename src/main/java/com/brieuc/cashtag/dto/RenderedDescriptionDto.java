package com.brieuc.cashtag.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Period description with the {{ }} expressions replaced by computed amounts")
public record RenderedDescriptionDto(
      @Schema(description = "Rendered text", example = "J'ai dépensé 1'234.50 CHF ce mois-ci", nullable = true)
      String rendered) {
}
