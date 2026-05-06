package com.brieuc.cashtag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a monetary currency")
public class CurrencyDto {
    @Schema(description = "ISO 4217 currency code (3 uppercase letters)", example = "CHF", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 3, maxLength = 3)
    private String code;

    @Schema(description = "Whether this currency is the reference currency")
    private Boolean reference;
}