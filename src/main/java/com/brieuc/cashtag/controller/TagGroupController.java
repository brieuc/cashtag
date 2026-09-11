package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.controller.api.TagGroupApi;
import com.brieuc.cashtag.dto.TagGroupDto;
import com.brieuc.cashtag.dto.TagGroupTitleSuggestionDto;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.mapper.TagGroupMapper;
import com.brieuc.cashtag.mapper.TagGroupTitleSuggestionMapper;
import com.brieuc.cashtag.repository.TagRepository;
import com.brieuc.cashtag.service.TagGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class TagGroupController implements TagGroupApi {

    private final TagGroupService tagGroupService;
    private final TagGroupMapper tagGroupMapper;
    private final TagRepository tagRepository;
    private final TagGroupTitleSuggestionMapper titleSuggestionMapper;

    @Override
    public ResponseEntity<List<TagGroupDto>> getTagGroups(Set<Long> tagIds) {
        List<Tag> tags = tagIds.stream().map(tagId -> tagRepository.findById(tagId).orElseThrow(() -> new RuntimeException(""))).toList();
        List<TagGroupDto> result = tagGroupService.getTagGroups(tags).stream()
                .map(tagGroupMapper::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<TagGroupTitleSuggestionDto>> getTitleSuggestions(@PathVariable Long tagGroupId) {
        List<TagGroupTitleSuggestionDto> result = tagGroupService.getTitleSuggestions(tagGroupId).stream()
                .map(titleSuggestionMapper::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }
}
