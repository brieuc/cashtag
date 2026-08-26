package com.brieuc.cashtag.service.helper;

import java.math.BigDecimal;

import com.brieuc.cashtag.entity.Tag;

public record TagAmount(
      Tag tag,
      BigDecimal amount
) {
      
};
