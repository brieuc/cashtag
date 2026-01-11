package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.TagMapper;
import com.brieuc.cashtag.service.TagServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "API de gestion des tags/étiquettes")
@RestController
@RequestMapping(value = "/tags", produces = "application/json")
@RequiredArgsConstructor
public class TagController {

    private final TagServiceImpl tagService;
    private final TagMapper tagMapper;
    private final PageRequestMapper pageRequestMapper;

    @Operation(summary = "Récupérer tous les tags", description = "Retourne une liste paginée de tous les tags")
    @GetMapping
    public ResponseEntity<PageImpl<TagDto>> getTags(
            @Parameter(description = "Paramètres de pagination") @ParameterObject PageRequestDto pageRequestDto) {

        Specification<Tag> specification = Specification.unrestricted();
        Page<TagDto> tags = tagService.getTags(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(tagMapper::toDto);

        return ResponseEntity.ok(new PageImpl<>(tags.getContent(), tags.getPageable(), tags.getTotalElements()));
    }

    @Operation(summary = "Récupérer un tag par son ID", description = "Retourne un tag spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDto.class))),
            @ApiResponse(responseCode = "404", description = "Tag non trouvé", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTagById(
            @Parameter(description = "ID du tag", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(tagMapper.toDto(tagService.getById(id)));
    }

    @Operation(summary = "Créer un nouveau tag", description = "Crée un nouveau tag/étiquette")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tag créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<TagDto> createTag(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Informations du tag à créer", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDto.class)))
            @RequestBody TagDto tagDto) {
        Tag saved = tagService.create(tagMapper.toEntity(tagDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(tagMapper.toDto(saved));
    }

    @Operation(summary = "Mettre à jour un tag", description = "Met à jour un tag existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag mis à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag non trouvé", content = @Content)
    })
    @PutMapping(value = "/{id}",  consumes = "application/json")
    public ResponseEntity<TagDto> updateTag(
            @Parameter(description = "ID du tag à mettre à jour", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nouvelles informations du tag", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDto.class)))
            @RequestBody TagDto tagDto) {
        if (!tagDto.getId().equals(id))
            throw new RuntimeException("no tag corresponding to this id " + id);
        Tag tag = tagService.update(tagMapper.toEntity(tagDto));
        return ResponseEntity.ok(tagMapper.toDto(tag));
        /*
        return tagService.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.getTitle());
                    existing.setDescription(dto.getDescription());
                    existing.setIcon(dto.getIcon());
                    Tag updated = tagService.save(existing);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
        */
    }

    @Operation(summary = "Supprimer un tag", description = "Supprime un tag définitivement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tag supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag non trouvé", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @Parameter(description = "ID du tag à supprimer", required = true, example = "1") @PathVariable Long id) {
        tagService.deleteById(id);
        return ResponseEntity.noContent().build();
        /*
        if (tagService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        tagService.deleteById(id);
        return ResponseEntity.noContent().build();
         */
    }
}