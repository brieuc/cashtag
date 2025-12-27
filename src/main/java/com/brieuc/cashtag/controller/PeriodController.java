package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.PeriodDto;
import com.brieuc.cashtag.entity.Period;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.PeriodMapper;
import com.brieuc.cashtag.service.PeriodServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Periods", description = "API de gestion des périodes comptables")
@RestController
@RequestMapping(value = "/periods", produces = "application/json")
@RequiredArgsConstructor
public class PeriodController {

    private final PeriodServiceImpl periodService;
    private final PeriodMapper periodMapper;
    private final PageRequestMapper pageRequestMapper;

    @Operation(summary = "Récupérer toutes les périodes", description = "Retourne une liste paginée de toutes les périodes comptables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des périodes récupérée avec succès",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PeriodDto.class))))
    })
    @GetMapping
    public ResponseEntity<Page<PeriodDto>> getPeriods(
            @Parameter(description = "Paramètres de pagination") @ParameterObject PageRequestDto pageRequestDto) {

        Specification<Period> specification = Specification.unrestricted();
        Page<PeriodDto> periods = periodService.getPeriods(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(periodMapper::toDto);

        return ResponseEntity.ok(periods);
    }

    @Operation(summary = "Récupérer une période par son ID", description = "Retourne une période spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Période trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class))),
            @ApiResponse(responseCode = "404", description = "Période non trouvée", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PeriodDto> getPeriodById(
            @Parameter(description = "ID de la période", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(periodMapper.toDto(periodService.getById(id)));
    }

    @Operation(summary = "Récupérer une période par date", description = "Retourne la période comptable contenant une date spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Période trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class))),
            @ApiResponse(responseCode = "404", description = "Aucune période ne contient cette date", content = @Content)
    })
    @GetMapping("/by-date")
    public ResponseEntity<PeriodDto> getPeriodByDate(
            @Parameter(description = "Date à rechercher", required = true, example = "2024-01-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return periodService.findPeriodByDate(date)
                .map(p -> ResponseEntity.ok(periodMapper.toDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer une nouvelle période", description = "Crée une nouvelle période comptable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Période créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<PeriodDto> createPeriod(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Informations de la période à créer", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class)))
            @RequestBody PeriodDto periodDto) {
        Period saved = periodService.create(periodMapper.toEntity(periodDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(periodMapper.toDto(saved));
    }

    @Operation(summary = "Mettre à jour une période", description = "Met à jour une période comptable existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Période mise à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Période non trouvée", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<PeriodDto> updatePeriod(
            @Parameter(description = "ID de la période à mettre à jour", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nouvelles informations de la période", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PeriodDto.class)))
            @RequestBody PeriodDto periodDto) {
        if (!periodDto.getId().equals(id))
            throw new RuntimeException("no period corresponding to this id");
        Period period = periodService.update(periodMapper.toEntity(periodDto));
        return ResponseEntity.ok(periodMapper.toDto(period));
    }

    @Operation(summary = "Supprimer une période", description = "Supprime une période comptable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Période supprimée avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Période non trouvée", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePeriod(
            @Parameter(description = "ID de la période à supprimer", required = true, example = "1") @PathVariable Long id) {
        periodService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
