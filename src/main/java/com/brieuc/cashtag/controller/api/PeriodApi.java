package com.brieuc.cashtag.controller.api;

import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.PeriodDto;
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

import java.time.LocalDate;

@Tag(name = "Periods", description = "API de gestion des périodes comptables")
@RequestMapping(value = "/periods", produces = "application/json")
public interface PeriodApi {

    @Operation(summary = "Récupérer toutes les périodes", description = "Retourne une liste paginée de toutes les périodes comptables")
    @GetMapping
    ResponseEntity<PageImpl<PeriodDto>> getPeriods(
            @Parameter(description = "Paramètres de pagination") @ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "Récupérer une période par son ID", description = "Retourne une période spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Période trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class))),
            @ApiResponse(responseCode = "404", description = "Période non trouvée", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<PeriodDto> getPeriodById(
            @Parameter(description = "ID de la période", required = true, example = "1") @PathVariable Long id);

    @Operation(summary = "Récupérer une période par date", description = "Retourne la période comptable contenant une date spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Période trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class))),
            @ApiResponse(responseCode = "404", description = "Aucune période ne contient cette date", content = @Content)
    })
    @GetMapping("/by-date")
    ResponseEntity<PeriodDto> getPeriodByDate(
            @Parameter(description = "Date à rechercher", required = true, example = "2024-01-15")
            @RequestParam LocalDate date);

    @Operation(summary = "Créer une nouvelle période", description = "Crée une nouvelle période comptable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Période créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @RequestBody(description = "Informations de la période à créer", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class)))
    @PostMapping(consumes = "application/json")
    ResponseEntity<PeriodDto> createPeriod(PeriodDto periodDto);

    @Operation(summary = "Mettre à jour une période", description = "Met à jour une période comptable existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Période mise à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Période non trouvée", content = @Content)
    })
    @RequestBody(description = "Nouvelles informations de la période", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class)))
    @PutMapping(value = "/{id}", consumes = "application/json")
    ResponseEntity<PeriodDto> updatePeriod(
            @Parameter(description = "ID de la période à mettre à jour", required = true, example = "1") @PathVariable Long id,
            PeriodDto periodDto);

    @Operation(summary = "Supprimer une période", description = "Supprime une période comptable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Période supprimée avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Période non trouvée", content = @Content)
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePeriod(
            @Parameter(description = "ID de la période à supprimer", required = true, example = "1") @PathVariable Long id);
}
