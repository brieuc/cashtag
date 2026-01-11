package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.service.CurrencyService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TagMapperImpl implements TagMapper {

      private final CurrencyService currencyService;
      private final CurrencyMapper currencyMapper;

      @Override
      public Tag toEntity(TagDto tagDto) {
            Tag tag = Tag.builder()
                  .id(tagDto.getId())
                  .description(tagDto.getDescription())
                  .icon(tagDto.getIcon())
                  .title(tagDto.getTitle())
                  .sortingOrder(tagDto.getSortingOrder())
                  .currency(tagDto.getCurrency() != null ? currencyService.getCurrencyByCode(tagDto.getCurrency().getCode()) : null)
                  .isCumulative(tagDto.getIsCumulative())
                  .hidden(tagDto.getHidden())
                  .build();
            return tag;
      }

      @Override
      public TagDto toDto(Tag tag) {
            TagDto tagDto = TagDto.builder()
                  .id(tag.getId())
                  .title(tag.getTitle())
                  .description(tag.getDescription())
                  .icon(tag.getIcon())
                  .sortingOrder(tag.getSortingOrder())
                  .currency(tag.getCurrency() != null ? currencyMapper.toDto(tag.getCurrency()) : null)
                  .isCumulative(tag.getIsCumulative())
                  .hidden(tag.getHidden())
                  .build();
            return tagDto;
      }
}
