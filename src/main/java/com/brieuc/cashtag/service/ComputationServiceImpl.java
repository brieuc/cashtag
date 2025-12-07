package com.brieuc.cashtag.service;

import java.math.BigDecimal;
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
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.mapper.EntrySpecificationMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComputationServiceImpl implements ComputationService {

      private final EntryService entryService;
      private final EntrySpecificationMapper entrySpecificationMapper;

      @Override
      public ComputationResponseDto computeSum(ComputationRequestDto computationRequestDto) {
            EntrySpecificationDto entrySpecificationDto = EntrySpecificationDto.builder()
                                                                  .startDate(computationRequestDto.startDate())
                                                                  .endDate(computationRequestDto.endDate())
                                                                  .tagIds(computationRequestDto.tags().stream().map(t -> t.getId()).collect(Collectors.toSet()))
                                                                  .build();

            Specification<Entry> specification = entrySpecificationMapper.toEntity(entrySpecificationDto);
            List<Entry> entries = entryService.getEntries(specification, Pageable.unpaged()).getContent();
            long numberOfEntries = entries.size();
            BigDecimal totalAmount = entries.stream().map(e -> e.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, ComputationCurrencyAmountDto> computationByCurrency = computeEntriesByCurrency(entries);
            return new ComputationResponseDto(
                  computationRequestDto.startDate(),
                  computationRequestDto.endDate(),
                  totalAmount,
                  numberOfEntries,
                  computationByCurrency
            );
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
