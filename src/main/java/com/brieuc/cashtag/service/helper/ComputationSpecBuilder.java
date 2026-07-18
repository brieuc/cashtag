package com.brieuc.cashtag.service.helper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.mapper.EntrySpecificationMapper;

import lombok.RequiredArgsConstructor;

/**
 * Builds the (unpaged) specification of the entries to compute from a computation request.
 * Carries the computation-specific filtering rules, in particular the cumulative tag case.
 */
@Component
@RequiredArgsConstructor
public class ComputationSpecBuilder {

      private final EntrySpecificationMapper entrySpecificationMapper;

      public Specification<Entry> from(ComputationRequestDto request) {
            LocalDateTime fromDate = request.startDate();
            Set<TagDto> tags = request.tags();

            // Special case: when a single cumulative tag is filtered, those tags keep the balance
            // along the way, so we must compute from the beginning (drop the start date).
            if (tags != null && tags.size() == 1) {
                  Optional<TagDto> cumulativeTag = tags.stream().filter(t -> t.getIsCumulative()).findFirst();
                  if (cumulativeTag.isPresent()) {
                        fromDate = null;
                  }
            }

            EntrySpecificationDto entrySpecificationDto = EntrySpecificationDto.builder()
                        .startDate(fromDate)
                        .endDate(request.endDate())
                        .searchText(request.searchText())
                        .currencyCodes(request.currencies() != null
                              ? request.currencies().stream().map(c -> c.getCode()).collect(Collectors.toSet())
                              : null)
                        .build();

            // Tags are managed separately: an empty collection breaks the query, so set them only if present.
            if (tags != null && !tags.isEmpty()) {
                  entrySpecificationDto.setTagIds(tags.stream().map(t -> t.getId()).collect(Collectors.toSet()));
            }

            return entrySpecificationMapper.toEntity(entrySpecificationDto);
      }
}
