package com.brieuc.cashtag.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.calculation.ComputationCurrencyAmountDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.service.helper.ComputeResult;
import com.brieuc.cashtag.service.helper.TagsAmount;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComputationServiceImpl implements ComputationService {

      private final CurrencyService currencyService;
      private final RateService rateService;

      @Override
      public ComputeResult computeSum(List<Entry> entries, String targetCurrencyCode) {

            long numberOfEntries = entries.size();
            BigDecimal totalAmount = entries.stream().map(e -> getLocalizedAmount(e, targetCurrencyCode)).reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, ComputationCurrencyAmountDto> computationByCurrency = computeEntriesByCurrency(entries);
            return new ComputeResult(
                  totalAmount,
                  targetCurrencyCode,
                  numberOfEntries,
                  computationByCurrency
            );
      }

      @Override
      public List<TagsAmount> getTagsAmounts(List<Entry> entries, List<Long> tagIds, List<Long> excludedTagIds, String targetCurrencyCode) {
            
            Set<Tag> tags = entries.stream().flatMap(e -> e.getTags().stream()).collect(Collectors.toSet());
            /*
            List<TagAmount> tagAmounts = new ArrayList<>();

            // First operation to find the entries for each tag
            for (Tag tag : tags) {
                  //BigDecimal amount = entries.stream().filter(e -> e.getTags().contains(tag)).map(e -> getLocalizedAmount(e, targetCurrencyCode)).reduce(BigDecimal.ZERO, BigDecimal::add);
                  TagAmount tagAmount = new TagAmount(tag, entries.stream().filter(e -> e.getTags().contains(tag)).toList(), false);
                  tagAmounts.add(tagAmount);
            }
            */


            HashMap<Set<Long>, Set<Tag>> mapEntriesIdTags = new HashMap<>();
            // First operation to find the entries for each tag
            for (Tag tag : tags) {
                  // Take the entries from the tag, this tag could not be already in the map because we're looping
                  // from a tags set. Meaning we don't have to check for the key against this tag.
                  // What we're interesting in, the entries that are the sames between several tags.
                  Set<Long> entriesId = entries.stream().filter(e -> e.getTags().contains(tag)).map(e -> e.getId()).collect(Collectors.toSet());
                  if (mapEntriesIdTags.containsKey(entriesId)) {
                        Set<Tag> mapTags = mapEntriesIdTags.get(entriesId);
                        mapTags.add(tag);
                  }
                  else {
                        mapEntriesIdTags.put(entriesId, new HashSet<>(Set.of(tag)));
                  }
            }
/*
            HashMap<Set<Tag>, List<Entry>> map = new HashMap<>();
            for (Entry entry : entries) {
                  Set<Tag> tagsWithout = entry.getTags().stream().filter(tag -> !tagIds.contains(tag)).collect(Collectors.toSet());
                  if (map.containsKey(tagsWithout)) {
                        map.get(entry.getTags()).add(entry);
                  }
                  else {
                        List<Entry> entriesForTags = new ArrayList<>();
                        entriesForTags.add(entry);
                        map.put(tagsWithout, entriesForTags);
                  }
            }

 */
            List<TagsAmount> tagsAmounts = new ArrayList<>();
            for (Map.Entry<Set<Long>, Set<Tag>> mapEntry : mapEntriesIdTags.entrySet()) {
                  Set<Long> entriesId = mapEntry.getKey();
                  List<Entry> mapEntries = entries.stream().filter(e -> entriesId.contains(e.getId())).toList();

                  BigDecimal amount = mapEntries.stream().map(e -> getLocalizedAmount(e, targetCurrencyCode)).reduce(BigDecimal.ZERO, BigDecimal::add);
                  TagsAmount tagsAmount = new TagsAmount(mapEntry.getValue().stream().toList(), amount);
                  tagsAmounts.add(tagsAmount);
            }

/*

            List<TagsAmount> tagsAmounts = new ArrayList<>();
            for (Map.Entry<Set<Tag>, List<Entry>> mapEntry : map.entrySet()) {
                  List<Entry> mapEntries = mapEntry.getValue();
                  BigDecimal amount = mapEntries.stream().map(e -> getLocalizedAmount(e, targetCurrencyCode)).reduce(BigDecimal.ZERO, BigDecimal::add);
                  TagsAmount tagsAmount = new TagsAmount(mapEntry.getKey().stream().toList(), amount);
                  tagsAmounts.add(tagsAmount);
            }
 */

            return tagsAmounts.stream()
                  .filter(ta -> !tagIds.containsAll(ta.tags().stream().map(t -> t.getId()).toList()))
                  .filter(ta -> !excludedTagIds.containsAll(ta.tags().stream().map(t -> t.getId()).toList()))
                  .toList();


            /*
            return tagAmounts.stream()
                  .filter(ta -> !tagIds.contains(ta.tag().getId()))
                  .filter(ta -> !excludedTagIds.contains(ta.tag().getId()))
                  .toList();
            */
      }
      /**
       *
       * @param Entry e
       * @param String targetCurrencyCode
       * @return the amount converted to the target currency
       */
      private BigDecimal getLocalizedAmount(Entry entry, String targetCurrencyCode) {

            // 1) The target currency is the entry currency. Could be reference currency CHF or MXN abroad.
            // 2) The target currency is different, i.e. MXN but we want to see the amount in CHF reference currency
            // The target currency is MXN, some entries are in CHF but the selected Tag has MXN currency set so we should convert CHF in MXN.
            //    - Edge case, the tag ("America") also has USD entries, we don't have any rate for USD -> MXN. If it happens (should be rare)
            //    we need to merge USD -> CHF and MXN -> CHF to find the right rate.

            String sourceCurrencyCode = entry.getCurrency().getCode();
            BigDecimal amount = entry.getAmount();
            if (sourceCurrencyCode.equals(targetCurrencyCode)) // 1)
                  return amount;

            BigDecimal rateValue;
            Currency referenceCurrency = currencyService.getReferenceCurrency();
            if (!targetCurrencyCode.equals(referenceCurrency.getCode())) { // and the entry currency is not the same as the reference (see above).
                  // This exception means we're in a case we're unable to convert the entry amount because the target currency is either
                  // not the reference currency (for which we have rates) or the entry currency for which we don't need to convert.
                  // The only exception we allow is the entry we're treating is CHF (we don't have a rate for CHF -> MXN but we are able to
                  // find it by applying inverse rate.
                  if (!sourceCurrencyCode.equals(referenceCurrency.getCode()))
                        throw new EntityNotFoundException("the target currency doesn't match the reference currency or the tag currency");
                  
                  Rate rate = rateService.getRateByCurrenciesAndDate(targetCurrencyCode, sourceCurrencyCode,entry.getAccountingDate().toLocalDate()); // 2)     
                  rateValue = BigDecimal.valueOf(1.00).divide(rate.getRate(), 2, RoundingMode.HALF_UP);
            }
            else {
                  Rate rate = rateService.getRateByCurrenciesAndDate(sourceCurrencyCode, targetCurrencyCode, entry.getAccountingDate().toLocalDate()); // 2)
                  rateValue = rate.getRate();
            }
            return amount.multiply(rateValue, MathContext.DECIMAL64);
      }

      private Map<String, ComputationCurrencyAmountDto> computeEntriesByCurrency(List<Entry> entries) {

            Map<String, ComputationCurrencyAmountDto> currencyMap = new HashMap<>();
            Set<String> currencyCodes = entries.stream().map(e -> e.getCurrency().getCode()).collect(Collectors.toSet());
            for (String currencyCode : currencyCodes) {
                  Set<Entry> entriesSet = entries.stream().filter(e -> e.getCurrency().getCode().equals(currencyCode)).collect(Collectors.toSet());
                  long numberOfEntries = entriesSet.size();
                  BigDecimal totalAmount = entriesSet.stream().map(e -> e.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                  ComputationCurrencyAmountDto computationCurrencyAmountDto = new ComputationCurrencyAmountDto(currencyCode, numberOfEntries, totalAmount);
                  currencyMap.put(currencyCode, computationCurrencyAmountDto);
            }
            return currencyMap;
      }
}
