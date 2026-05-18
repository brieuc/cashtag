package com.brieuc.cashtag.controller.api;

import com.brieuc.cashtag.dto.TagGroupDto;
import com.brieuc.cashtag.dto.TagGroupSpecificationDto;
import com.brieuc.cashtag.dto.TagGroupTitleSuggestionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Tag Groups", description = "API de gestion des groupes de tags")
@RequestMapping(value = "/tag-groups", produces = "application/json")
public interface TagGroupApi {

    @Operation(summary = "Récupérer les groupes de tags contenant au moins un des tags donnés")
    @GetMapping
    ResponseEntity<List<TagGroupDto>> getTagGroups(
            @Parameter(description = "Critères de filtrage") @ParameterObject TagGroupSpecificationDto specification);

    @Operation(summary = "Récupérer les suggestions de titres pour un groupe de tags, triées par popularité")
    @GetMapping("/{id}/title-suggestions")
    ResponseEntity<List<TagGroupTitleSuggestionDto>> getTitleSuggestions(
            @Parameter(description = "ID du groupe de tags", required = true) @PathVariable Long id);
}
