package com.brieuc.cashtag.controller.api;

import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.TagDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Tags", description = "API de gestion des tags/étiquettes")
@RequestMapping(value = "/tags", produces = "application/json")
public interface TagApi {

    @Operation(summary = "Récupérer tous les tags", description = "Retourne une liste paginée de tous les tags")
    @GetMapping
    ResponseEntity<PageImpl<TagDto>> getTags(
            @Parameter(description = "Paramètres de pagination") @ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "Récupérer un tag par son ID", description = "Retourne un tag spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDto.class))),
            @ApiResponse(responseCode = "404", description = "Tag non trouvé", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<TagDto> getTagById(
            @Parameter(description = "ID du tag", required = true, example = "1") @PathVariable Long id);

    @Operation(summary = "Créer un nouveau tag", description = "Crée un nouveau tag/étiquette")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tag créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @RequestBody(description = "Informations du tag à créer", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDto.class)))
    @PostMapping(consumes = "application/json")
    ResponseEntity<TagDto> createTag(TagDto tagDto);

    @Operation(summary = "Uploader une icône pour un tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Icône uploadée avec succès"),
            @ApiResponse(responseCode = "404", description = "Tag non trouvé"),
            @ApiResponse(responseCode = "400", description = "Fichier invalide")
    })
    @PostMapping(value = "/{id}/icon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<TagDto> uploadIcon(
            @Parameter(description = "Tag id") @PathVariable Long id,
            @RequestParam("file") MultipartFile file);

    @Operation(summary = "Mettre à jour un tag", description = "Met à jour un tag existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag mis à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag non trouvé", content = @Content)
    })
    @RequestBody(description = "Nouvelles informations du tag", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDto.class)))
    @PutMapping(value = "/{id}", consumes = "application/json")
    ResponseEntity<TagDto> updateTag(
            @Parameter(description = "ID du tag à mettre à jour", required = true, example = "1") @PathVariable Long id,
            TagDto tagDto);

    @Operation(summary = "Supprimer un tag", description = "Supprime un tag définitivement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tag supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag non trouvé", content = @Content)
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTag(
            @Parameter(description = "ID du tag à supprimer", required = true, example = "1") @PathVariable Long id);
}
