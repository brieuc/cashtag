package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.RecurrencyDto;
import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Recurrency;
import com.brieuc.cashtag.service.CurrencyService;
import com.brieuc.cashtag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecurrencyMapperImpl implements RecurrencyMapper {

    private final CurrencyService currencyService;
    private final TagMapper tagMapper;
    private final TagService tagService;

    @Override
    public Recurrency toEntity(RecurrencyDto recurrencyDto) {
        return Recurrency.builder()
                .id(recurrencyDto.getId())
                .modificationDate(recurrencyDto.getModificationDate())
                .title(recurrencyDto.getTitle())
                .description(recurrencyDto.getDescription())
                .amount(recurrencyDto.getAmount())
                .currency(currencyService.getCurrencyByCode(recurrencyDto.getCurrencyCode()))
                .startDate(recurrencyDto.getStartDate())
                .frequency(recurrencyDto.getFrequency())
                .tags(recurrencyDto.getTags() == null ? null : recurrencyDto.getTags().stream()
                        .map(t -> tagService.getById(t.getId()))
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public RecurrencyDto toDto(Recurrency recurrency) {
        return RecurrencyDto.builder()
                .id(recurrency.getId())
                .modificationDate(recurrency.getModificationDate())
                .title(recurrency.getTitle())
                .description(recurrency.getDescription())
                .amount(recurrency.getAmount())
                .currencyCode(recurrency.getCurrency().getCode())
                .startDate(recurrency.getStartDate())
                .frequency(recurrency.getFrequency())
                .tags(recurrency.getTags() == null ? null : recurrency.getTags().stream()
                        .map(tagMapper::toDto)
                        .sorted(Comparator.comparing(TagDto::getSortingOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList())
                .build();
    }
}
