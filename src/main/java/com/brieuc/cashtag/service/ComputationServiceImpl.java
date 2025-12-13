package com.brieuc.cashtag.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.dto.calculation.ComputationCurrencyAmountDto;
import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.mapper.EntrySpecificationMapper;
import com.brieuc.cashtag.repository.CurrencyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComputationServiceImpl implements ComputationService {

      private final CurrencyService currencyService;
      private final RateService rateService;
      private final EntryService entryService;
      private final EntrySpecificationMapper entrySpecificationMapper;

      @Override
      public ComputationResponseDto computeSum(ComputationRequestDto computationRequestDto) {
            EntrySpecificationDto entrySpecificationDto = EntrySpecificationDto.builder()
                                                                  .startDate(computationRequestDto.startDate())
                                                                  .endDate(computationRequestDto.endDate())
                                                                  .build();

            // We need to manage the tags separately since an empty collection doesn't work for the
            // request. We add the tag only if we have them.
            if (computationRequestDto.tags() != null) {
                  entrySpecificationDto.setTagIds(computationRequestDto.tags().stream().map(t -> t.getId()).collect(Collectors.toSet()));
            }                                                              

            Specification<Entry> specification = entrySpecificationMapper.toEntity(entrySpecificationDto);
            
            // The target currency is contained in the request, could be any currency
            //Currency referenceCurrency = currencyService.getReferenceCurrency();
            
            List<Entry> entries = entryService.getEntries(specification, Pageable.unpaged()).getContent();
            long numberOfEntries = entries.size();
            BigDecimal totalAmount = entries.stream().map(e -> getLocalizedAmount(e, computationRequestDto.targetCurrencyCode())).reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, ComputationCurrencyAmountDto> computationByCurrency = computeEntriesByCurrency(entries);
            return new ComputationResponseDto(
                  computationRequestDto.startDate(),
                  computationRequestDto.endDate(),
                  totalAmount,
                  computationRequestDto.targetCurrencyCode(),
                  numberOfEntries,
                  computationByCurrency
            );
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
            return amount.divide(rateValue, 2, RoundingMode.HALF_UP);
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
            return null;
      }
}
