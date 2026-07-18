package com.brieuc.cashtag.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.service.helper.TagAmount;

@Service
public interface ComputationService {
      /*
      Compute the sum of the period entries. We could say no tag
      no amount but for the display, it's better to get an amout and
      it's more consistent with computeByCurrency. If tags is null
      then all the entries are computed.
      */
      ComputationResponseDto computeSum(List<Entry> entries, String targetCurrencyCode, LocalDateTime startDate, LocalDateTime endDate);
      List<TagAmount> geTagAmounts(List<Entry> entries);
}
