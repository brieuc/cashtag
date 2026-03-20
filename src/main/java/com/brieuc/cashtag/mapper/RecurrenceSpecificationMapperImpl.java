package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.RecurrenceSpecificationDto;
import com.brieuc.cashtag.entity.Frequency;
import com.brieuc.cashtag.entity.Recurrence;
import com.brieuc.cashtag.entity.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class RecurrenceSpecificationMapperImpl implements RecurrenceSpecificationMapper {

    @Override
    public Specification<Recurrence> toEntity(RecurrenceSpecificationDto dto) {
        return hasStartDateBetween(dto.getStartDate(), dto.getEndDate())
                .and(hasAllTags(dto.getTagIds()))
                .and(hasFrequencies(dto.getFrequencies()));
    }

    public Specification<Recurrence> hasStartDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate != null && endDate != null) {
                return cb.and(
                        cb.greaterThanOrEqualTo(root.get("startDate"), startDate),
                        cb.lessThanOrEqualTo(root.get("startDate"), endDate)
                );
            } else if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("startDate"), startDate);
            } else if (endDate != null) {
                return cb.lessThanOrEqualTo(root.get("startDate"), endDate);
            } else {
                return cb.conjunction();
            }
        };
    }

    public static Specification<Recurrence> hasAllTags(Set<Long> tagIds) {
        if (Objects.isNull(tagIds) || tagIds.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            if (query == null) return cb.conjunction();
            List<Predicate> predicates = new ArrayList<>();
            for (Long tagId : tagIds) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Recurrence> subRoot = subquery.correlate(root);
                Join<Recurrence, Tag> subTags = subRoot.join("tags");
                subquery.select(cb.literal(1L))
                        .where(cb.equal(subTags.get("id"), tagId));
                predicates.add(cb.exists(subquery));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Recurrence> hasFrequencies(Set<Frequency> frequencies) {
        if (Objects.isNull(frequencies) || frequencies.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> root.get("frequency").in(frequencies);
    }
}
