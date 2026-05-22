package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.entity.TagGroup;
import com.brieuc.cashtag.entity.TagGroupTitleSuggestion;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.TagGroupRepository;
import com.brieuc.cashtag.repository.TagGroupTitleSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagGroupServiceImpl implements TagGroupService {

    private final TagGroupRepository tagGroupRepository;
    private final TagGroupTitleSuggestionRepository titleSuggestionRepository;

    @Override
    public void recordTags(Set<Tag> tags, String title) {
        if (tags == null || tags.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        TagGroup tagGroup = resolveTagGroup(tags, now);
        resolveTitleSuggestion(tagGroup, title, now);
    }

    @Override
    public List<TagGroup> getTagGroups(Specification<TagGroup> specification) {
        return tagGroupRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "usageCount"));
    }

    @Override
    public List<TagGroupTitleSuggestion> getTitleSuggestions(Long tagGroupId) {
        TagGroup tagGroup = tagGroupRepository.findById(tagGroupId)
                .orElseThrow(() -> new EntityNotFoundException("No tag group id " + tagGroupId + " was found"));
        return titleSuggestionRepository.findByTagGroupOrderByUsageCountDesc(tagGroup);
    }

    private TagGroup resolveTagGroup(Set<Tag> tags, LocalDateTime now) {
        TagGroup tagGroup = tagGroupRepository.findByExactTags(tags)
                .map(g -> {
                    g.setUsageCount(g.getUsageCount() + 1);
                    g.setLastUsed(now);
                    return g;
                })
                .orElseGet(() -> TagGroup.builder()
                        .tags(new HashSet<>(tags))
                        .usageCount(1)
                        .lastUsed(now)
                        .build());

        return tagGroupRepository.save(tagGroup);
    }

    private TagGroupTitleSuggestion resolveTitleSuggestion(TagGroup tagGroup, String title, LocalDateTime now) {
        TagGroupTitleSuggestion suggestion = titleSuggestionRepository.findByTagGroupAndTitle(tagGroup, title)
                .map(s -> {
                    s.setUsageCount(s.getUsageCount() + 1);
                    s.setLastUsed(now);
                    return s;
                })
                .orElseGet(() -> TagGroupTitleSuggestion.builder()
                        .tagGroup(tagGroup)
                        .title(title)
                        .usageCount(1)
                        .lastUsed(now)
                        .build());

        return titleSuggestionRepository.save(suggestion);
    }

    @Override
    public void resetTagGroupsAndTitleSuggestions(List<Entry> entries) {
        for (Entry entry : entries) {
            recordTags(entry.getTags(), entry.getTitle());
        }
    }
}
