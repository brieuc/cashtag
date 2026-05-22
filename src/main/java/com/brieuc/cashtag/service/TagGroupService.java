package com.brieuc.cashtag.service;

import java.util.List;
import java.util.Set;

import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.entity.TagGroup;
import com.brieuc.cashtag.entity.TagGroupTitleSuggestion;
import org.springframework.data.jpa.domain.Specification;

public interface TagGroupService {

      void recordTags(Set<Tag> tags, String title);

      List<TagGroup> getTagGroups(Specification<TagGroup> specification);

      List<TagGroupTitleSuggestion> getTitleSuggestions(Long tagGroupId);
      
      void resetTagGroupsAndTitleSuggestions(List<Entry> entries);
}
