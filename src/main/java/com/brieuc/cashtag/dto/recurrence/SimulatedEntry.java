package com.brieuc.cashtag.dto.recurrence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.brieuc.cashtag.entity.Tag;

public record SimulatedEntry (
      LocalDateTime accountingDate, // initial accounting date
      String title,
      String description,
      BigDecimal amount,
      String currencyCode,
      List<Tag> tags
) {};