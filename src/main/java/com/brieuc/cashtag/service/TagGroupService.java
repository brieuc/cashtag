package com.brieuc.cashtag.service;

import java.util.List;
import java.util.Set;

import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.entity.TagGroup;
import com.brieuc.cashtag.entity.TagGroupTitleSuggestion;

public interface TagGroupService {

      void recordTags(Set<Tag> tags, String title);

      List<TagGroup> getTagGroups(List<Tag> tags);

      List<TagGroupTitleSuggestion> getTitleSuggestions(Long tagGroupId);
      
      void resetTagGroupsAndTitleSuggestions(List<Entry> entries);
}