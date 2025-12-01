package com.brieuc.cashtag.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class PageRequestDto {  
    @Schema(defaultValue = "0")
    @Min(0)
    @Max(Integer.MAX_VALUE)
    Integer page;  
  
    @Min(1)  
    @Max(100)  
    Integer size;  
  
    List<String> sort;
}