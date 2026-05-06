package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.RecurrenceDto;
import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Recurrence;
import com.brieuc.cashtag.service.CurrencyService;
import com.brieuc.cashtag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecurrenceMapperImpl implements RecurrenceMapper {

    private final CurrencyService currencyService;
    private final TagMapper tagMapper;
    private final TagService tagService;

    @Override
    public Recurrence toEntity(RecurrenceDto recurrenceDto) {
        return Recurrence.builder()
                .id(recurrenceDto.getId())
                .modificationDate(recurrenceDto.getModificationDate())
                .title(recurrenceDto.getTitle())
                .description(recurrenceDto.getDescription())
                .amount(recurrenceDto.getAmount())
                .currency(currencyService.getCurrencyByCode(recurrenceDto.getCurrencyCode()))
                .startDate(recurrenceDto.getStartDate() != null ? recurrenceDto.getStartDate().atStartOfDay() : null)
                .frequency(recurrenceDto.getFrequency())
                .tags(recurrenceDto.getTags() == null ? null : recurrenceDto.getTags().stream()
                        .map(t -> tagService.getById(t.getId()))
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public RecurrenceDto toDto(Recurrence recurrence) {
        return RecurrenceDto.builder()
                .id(recurrence.getId())
                .modificationDate(recurrence.getModificationDate())
                .title(recurrence.getTitle())
                .description(recurrence.getDescription())
                .amount(recurrence.getAmount())
                .currencyCode(recurrence.getCurrency().getCode())
                .startDate(recurrence.getStartDate() != null ? recurrence.getStartDate().toLocalDate() : null)
                .frequency(recurrence.getFrequency())
                .tags(recurrence.getTags() == null ? null : recurrence.getTags().stream()
                        .map(tagMapper::toDto)
                        .sorted(Comparator.comparing(TagDto::getSortingOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList())
                .build();
    }
}
