package com.brieuc.cashtag.service.helper;

import java.math.BigDecimal;
import java.util.List;

import com.brieuc.cashtag.entity.Tag;

public record TagsAmount(
      List<Tag> tags,
      BigDecimal amount
) {
      
};
