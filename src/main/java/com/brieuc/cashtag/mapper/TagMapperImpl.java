package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.service.CurrencyService;
import com.brieuc.cashtag.service.TagImageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TagMapperImpl implements TagMapper {

      private final CurrencyService currencyService;
      private final TagImageService tagImageService;

      @Override
      public Tag toEntity(TagDto tagDto) {
            Tag tag = Tag.builder()
                  .id(tagDto.getId())
                  .description(tagDto.getDescription())
                  .title(tagDto.getTitle())
                  .sortingOrder(tagDto.getSortingOrder())
                  .currency(tagDto.getCurrencyCode() != null ? currencyService.getCurrencyByCode(tagDto.getCurrencyCode()) : null)
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
                  .icon(tagImageService.getTagImage(tag) != null ? tagImageService.getTagImage(tag).getImagePath() : null)
                  .sortingOrder(tag.getSortingOrder())
                  .currencyCode(tag.getCurrency() != null ? tag.getCurrency().getCode() : null)
                  .isCumulative(tag.getIsCumulative())
                  .hidden(tag.getHidden())
                  .build();
            return tagDto;
      }
}
