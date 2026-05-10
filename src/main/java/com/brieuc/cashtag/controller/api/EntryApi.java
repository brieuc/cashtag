package com.brieuc.cashtag.controller.api;

import com.brieuc.cashtag.dto.EntryDto;
import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.dto.PageRequestDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Entries", description = "API de gestion des entrées financières")
@RequestMapping(value = "/entries", produces = "application/json")
public interface EntryApi {

    @Operation(summary = "Récupérer toutes les entrées",
            description = "Retourne une liste paginée d'entrées filtrées selon les critères de recherche fournis")
    @GetMapping
    ResponseEntity<PageImpl<EntryDto>> getEntries(
            @Parameter(description = "Critères de filtrage des entrées") @ParameterObject EntrySpecificationDto entrySpecificationDto,
            @Parameter(description = "Paramètres de pagination (page, size, sort)") @ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "Récupérer une entrée par son ID", description = "Retourne une entrée spécifique identifiée par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrée trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntryDto.class))),
            @ApiResponse(responseCode = "404", description = "Entrée non trouvée", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<EntryDto> getEntryById(
            @Parameter(description = "ID de l'entrée à récupérer", required = true, example = "1") @PathVariable Long id);

    @Operation(summary = "Créer plusieurs entrées en lot", description = "Crée plusieurs entrées financières en une seule transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrées créées avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntryDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @RequestBody(description = "Liste des entrées à créer", required = true,
            content = @Content(mediaType = "application/json"))
    @PostMapping(value = "/batch", consumes = "application/json")
    ResponseEntity<List<EntryDto>> createBatch(List<EntryDto> entryDtos);

    @Operation(summary = "Créer une nouvelle entrée", description = "Crée une nouvelle entrée financière avec les informations fournies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrée créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntryDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @RequestBody(description = "Informations de l'entrée à créer", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntryDto.class)))
    @PostMapping(consumes = "application/json")
    ResponseEntity<EntryDto> createEntry(EntryDto entryDto);

    @Operation(summary = "Mettre à jour une entrée", description = "Met à jour une entrée existante avec les nouvelles informations fournies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrée mise à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntryDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou incohérence entre l'ID du path et l'ID du body", content = @Content),
            @ApiResponse(responseCode = "404", description = "Entrée non trouvée", content = @Content)
    })
    @RequestBody(description = "Nouvelles informations de l'entrée", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntryDto.class)))
    @PutMapping(value = "/{id}", consumes = "application/json")
    ResponseEntity<EntryDto> updateEntry(
            @Parameter(description = "ID de l'entrée à mettre à jour", required = true, example = "1") @PathVariable Long id,
            EntryDto entryDto);

    @Operation(summary = "Supprimer une entrée", description = "Supprime définitivement une entrée existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entrée supprimée avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Entrée non trouvée", content = @Content)
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEntry(
            @Parameter(description = "ID de l'entrée à supprimer", required = true, example = "1") @PathVariable Long id);
}
