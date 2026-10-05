package com.brieuc.cashtag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.service.helper.ComputeResult;
import com.brieuc.cashtag.service.helper.TagsAmount;

@Service
public interface ComputationService {
      /*
      Compute the sum of the period entries. We could say no tag
      no amount but for the display, it's better to get an amout and
      it's more consistent with computeByCurrency. If tags is null
      then all the entries are computed.
      */
      ComputeResult computeSum(List<Entry> entries, String targetCurrencyCode);
      List<TagsAmount> getTagsAmounts(List<Entry> entries, List<Long> tagIds, List<Long> excludedTagIds, String targetCurrencyCode);
}
