package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.TagGroupSpecificationDto;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.entity.TagGroup;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TagGroupSpecificationMapperImpl implements TagGroupSpecificationMapper {

    @Override
    public Specification<TagGroup> toEntity(TagGroupSpecificationDto tagGroupSpecificationDto) {
        return hasAllTags(tagGroupSpecificationDto.getTagIds());
    }

    private Specification<TagGroup> hasAllTags(Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return null;
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Long tagId : tagIds) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<TagGroup> subRoot = subquery.correlate(root);
                Join<TagGroup, Tag> subTags = subRoot.join("tags");
                subquery.select(cb.literal(1L))
                        .where(cb.equal(subTags.get("id"), tagId));
                predicates.add(cb.exists(subquery));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
