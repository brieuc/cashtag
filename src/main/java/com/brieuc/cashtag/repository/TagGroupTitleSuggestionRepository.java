package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.TagGroup;
import com.brieuc.cashtag.entity.TagGroupTitleSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagGroupTitleSuggestionRepository extends JpaRepository<TagGroupTitleSuggestion, Long> {

    Optional<TagGroupTitleSuggestion> findByTagGroupAndTitle(TagGroup tagGroup, String title);

    List<TagGroupTitleSuggestion> findByTagGroupOrderByUsageCountDesc(TagGroup tagGroup);
}
