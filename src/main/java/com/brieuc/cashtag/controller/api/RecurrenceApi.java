package com.brieuc.cashtag.controller.api;

import com.brieuc.cashtag.dto.EntryDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.RecurrenceDto;
import com.brieuc.cashtag.dto.RecurrenceSpecificationDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import java.util.List;

@Tag(name = "Recurrences", description = "API de gestion des transactions récurrentes")
@RequestMapping(value = "/recurrences", produces = "application/json")
public interface RecurrenceApi {

    @Operation(summary = "Récupérer toutes les récurrences",
            description = "Retourne une liste paginée de récurrences filtrées selon les critères fournis")
    @GetMapping
    ResponseEntity<PageImpl<RecurrenceDto>> getRecurrences(
            @Parameter(description = "Critères de filtrage des récurrences") @ParameterObject RecurrenceSpecificationDto recurrenceSpecificationDto,
            @Parameter(description = "Paramètres de pagination (page, size, sort)") @ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "Simuler les entrées récurrentes sur une période",
            description = "Retourne les entrées qui seraient générées par les récurrences actives entre fromDate et toDate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrées simulées",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EntryDto.class))))
    })
    @GetMapping("/simulate")
    ResponseEntity<List<EntryDto>> simulateEntries(
            @Parameter(description = "Date de début de la période (inclusive)", required = true, example = "2025-01-01")
            @RequestParam LocalDate fromDate,
            @Parameter(description = "Date de fin de la période (inclusive)", required = true, example = "2025-01-31")
            @RequestParam LocalDate toDate);

    @Operation(summary = "Récupérer une récurrence par son ID",
            description = "Retourne une récurrence spécifique identifiée par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Récurrence trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrenceDto.class))),
            @ApiResponse(responseCode = "404", description = "Récurrence non trouvée", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<RecurrenceDto> getRecurrenceById(
            @Parameter(description = "ID de la récurrence à récupérer", required = true, example = "1") @PathVariable Long id);

    @Operation(summary = "Créer une nouvelle récurrence",
            description = "Crée une nouvelle transaction récurrente avec les informations fournies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Récurrence créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrenceDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @RequestBody(description = "Informations de la récurrence à créer", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrenceDto.class)))
    @PostMapping(consumes = "application/json")
    ResponseEntity<RecurrenceDto> createRecurrence(RecurrenceDto recurrenceDto);

    @Operation(summary = "Mettre à jour une récurrence",
            description = "Met à jour une récurrence existante avec les nouvelles informations fournies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Récurrence mise à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrenceDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Récurrence non trouvée", content = @Content)
    })
    @RequestBody(description = "Nouvelles informations de la récurrence", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrenceDto.class)))
    @PutMapping(value = "/{id}", consumes = "application/json")
    ResponseEntity<RecurrenceDto> updateRecurrence(
            @Parameter(description = "ID de la récurrence à mettre à jour", required = true, example = "1") @PathVariable Long id,
            RecurrenceDto recurrenceDto);

    @Operation(summary = "Supprimer une récurrence", description = "Supprime définitivement une récurrence existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Récurrence supprimée avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Récurrence non trouvée", content = @Content)
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteRecurrence(
            @Parameter(description = "ID de la récurrence à supprimer", required = true, example = "1") @PathVariable Long id);
}
