package com.brieuc.cashtag.mapper;


import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.EntryDto;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.service.CurrencyService;
import com.brieuc.cashtag.service.TagService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EntryMapperImpl implements EntryMapper {

      private final CurrencyService currencyService;
      private final TagMapper tagMapper;
      private final TagService tagService;

      @Override
      public Entry toEntity(EntryDto entryDto) {
            Entry entry = Entry.builder()
                              .id(entryDto.getId())
                              .accountingDate(entryDto.getAccountingDate())
                              .modificationDate(entryDto.getModificationDate())
                              //.modificationDate(null) internally managed
                              .amount(entryDto.getAmount())
                              .title(entryDto.getTitle())
                              .description(entryDto.getDescription())
                              // Le problème c'est qu'on ne vérifie pas que le tag existe
                              //.tags(entryDto.getTags().stream().map(tagMapper::toEntity).collect(Collectors.toSet()))
                              .tags(entryDto.getTags().stream().map(t -> tagService.getById(t.getId())).collect(Collectors.toSet()))
                              .currency(currencyService.getCurrencyByCode(entryDto.getCurrencyCode()))
                              .build();

            return entry;
      }

      @Override
      public EntryDto tDto(Entry entry) {
            EntryDto entryDto = EntryDto.builder()
                              .id(entry.getId())
                              .accountingDate(entry.getAccountingDate())
                              .modificationDate(entry.getModificationDate())
                              .amount(entry.getAmount())
                              .currencyCode(entry.getCurrency().getCode())
                              .title(entry.getTitle())
                              .description(entry.getDescription())
                              .tags(entry.getTags().stream().map(tag -> tagMapper.toDto(tag)).collect(Collectors.toSet()))
                              .build();
            return entryDto;
      }
}
