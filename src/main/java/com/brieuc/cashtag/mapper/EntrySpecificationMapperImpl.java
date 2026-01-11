package com.brieuc.cashtag.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Tag;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class EntrySpecificationMapperImpl implements EntrySpecificationMapper {

      @Override
      public Specification<Entry> toEntity(EntrySpecificationDto entrySpecificationDto) {
            return hasDateBetween(entrySpecificationDto.getStartDate(),
                                    entrySpecificationDto.getEndDate())
                        .and(hasAllTags(entrySpecificationDto.getTagIds()));
      }

      /*
      public static Specification<Entry> hasDateBetween(LocalDateTime start, LocalDateTime end) {
            return (root, query, cb) -> cb.between(root.get("accountingDate"), start, end);
      }
      */

      public Specification<Entry> hasDateBetween(LocalDate startDate, LocalDate endDate) {
            return (root, query, cb) -> {
                  if (startDate != null && endDate != null) {
                        return cb.and(
                        cb.greaterThanOrEqualTo(root.get("accountingDate"), startDate.atStartOfDay()),
                        cb.lessThan(root.get("accountingDate"), endDate.plusDays(1).atStartOfDay())
                        );
                  } else if (startDate != null) {
                        return cb.greaterThanOrEqualTo(root.get("accountingDate"), startDate.atStartOfDay());
                  } else if (endDate != null) {
                        return cb.lessThan(root.get("accountingDate"), endDate.plusDays(1).atStartOfDay());
                  } else {
                        return cb.conjunction(); // Retourne toujours vrai si les deux sont null
                  }
            };
      }

      public static Specification<Entry> hasAllTags(Set<Long> tagIds) {
            if (Objects.isNull(tagIds) || tagIds.isEmpty()) {
                  return null;
            }
            return (root, query, cb) -> {
                  // Pour chaque tagId, on crée une subquery qui vérifie que l'entry possède ce tag
                  // L'entry doit satisfaire TOUTES les subqueries
                  List<Predicate> predicates = new ArrayList<>();
                  
                  for (Long tagId : tagIds) {
                        Subquery<Long> subquery = query.subquery(Long.class);
                        Root<Entry> subRoot = subquery.correlate(root);
                        Join<Entry, Tag> subTags = subRoot.join("tags");
                        
                        subquery.select(cb.literal(1L))
                              .where(cb.equal(subTags.get("id"), tagId));
                        
                        predicates.add(cb.exists(subquery));
                  }
                  
                  return cb.and(predicates.toArray(new Predicate[0]));
            };
      }
}
