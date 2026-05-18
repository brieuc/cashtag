package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.entity.TagGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TagGroupRepository extends JpaRepository<TagGroup, Long>, JpaSpecificationExecutor<TagGroup> {

    @Query("""
            SELECT tg FROM TagGroup tg
            WHERE SIZE(tg.tags) = :tagCount
            AND (SELECT COUNT(t) FROM TagGroup tg2 JOIN tg2.tags t WHERE tg2 = tg AND t IN :tags) = :tagCount
            """)
    Optional<TagGroup> findByExactTags(@Param("tags") Set<Tag> tags, @Param("tagCount") int tagCount);

    default Optional<TagGroup> findByExactTags(Set<Tag> tags) {
        return findByExactTags(tags, tags.size());
    }

}
