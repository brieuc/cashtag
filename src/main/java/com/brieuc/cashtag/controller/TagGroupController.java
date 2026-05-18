package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.controller.api.TagGroupApi;
import com.brieuc.cashtag.dto.TagGroupDto;
import com.brieuc.cashtag.dto.TagGroupSpecificationDto;
import com.brieuc.cashtag.dto.TagGroupTitleSuggestionDto;
import com.brieuc.cashtag.mapper.TagGroupMapper;
import com.brieuc.cashtag.mapper.TagGroupSpecificationMapper;
import com.brieuc.cashtag.mapper.TagGroupTitleSuggestionMapper;
import com.brieuc.cashtag.service.TagGroupServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagGroupController implements TagGroupApi {

    private final TagGroupServiceImpl tagGroupService;
    private final TagGroupMapper tagGroupMapper;
    private final TagGroupSpecificationMapper tagGroupSpecificationMapper;
    private final TagGroupTitleSuggestionMapper titleSuggestionMapper;

    @Override
    public ResponseEntity<List<TagGroupDto>> getTagGroups(TagGroupSpecificationDto specification) {
        List<TagGroupDto> result = tagGroupService.getTagGroups(tagGroupSpecificationMapper.toEntity(specification)).stream()
                .map(tagGroupMapper::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<TagGroupTitleSuggestionDto>> getTitleSuggestions(@PathVariable Long id) {
        List<TagGroupTitleSuggestionDto> result = tagGroupService.getTitleSuggestions(id).stream()
                .map(titleSuggestionMapper::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }
}
