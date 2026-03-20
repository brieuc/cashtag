package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.RecurrenceDto;
import com.brieuc.cashtag.dto.RecurrenceSpecificationDto;
import com.brieuc.cashtag.entity.Recurrence;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.RecurrenceMapper;
import com.brieuc.cashtag.mapper.RecurrenceSpecificationMapper;
import com.brieuc.cashtag.service.RecurrenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Recurrences", description = "API de gestion des transactions récurrentes")
@RestController
@RequestMapping(value = "/recurrences", produces = "application/json")
@RequiredArgsConstructor
public class RecurrenceController {

    private final RecurrenceService recurrenceService;
    private final PageRequestMapper pageRequestMapper;
    private final RecurrenceMapper recurrenceMapper;
    private final RecurrenceSpecificationMapper recurrenceSpecificationMapper;

    @Operation(
            summary = "Récupérer toutes les récurrences",
            description = "Retourne une liste paginée de récurrences filtrées selon les critères fournis"
    )
    @GetMapping
    public ResponseEntity<PageImpl<RecurrenceDto>> getRecurrences(
            @Parameter(description = "Critères de filtrage des récurrences")
            @ParameterObject RecurrenceSpecificationDto recurrenceSpecificationDto,
            @Parameter(description = "Paramètres de pagination (page, size, sort)")
            @ParameterObject PageRequestDto pageRequestDto) {
        Specification<Recurrence> specification = recurrenceSpecificationMapper.toEntity(recurrenceSpecificationDto);
        Page<RecurrenceDto> recurrences = recurrenceService.getRecurrences(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(recurrenceMapper::toDto);
        return ResponseEntity.ok(new PageImpl<>(recurrences.getContent(),
                                               recurrences.getPageable(),
                                               recurrences.getTotalElements()));
    }

    

    @Operation(
            summary = "Récupérer une récurrence par son ID",
            description = "Retourne une récurrence spécifique identifiée par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Récurrence trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrenceDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Récurrence non trouvée", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecurrenceDto> getRecurrenceById(
            @Parameter(description = "ID de la récurrence à récupérer", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(recurrenceMapper.toDto(recurrenceService.getById(id)));
    }

    @Operation(
            summary = "Créer une nouvelle récurrence",
            description = "Crée une nouvelle transaction récurrente avec les informations fournies"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Récurrence créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrenceDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<RecurrenceDto> createRecurrence(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations de la récurrence à créer",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrenceDto.class))
            )
            @RequestBody RecurrenceDto recurrenceDto) {
        Recurrence newRecurrence = recurrenceService.create(recurrenceMapper.toEntity(recurrenceDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(recurrenceMapper.toDto(newRecurrence));
    }

    @Operation(
            summary = "Mettre à jour une récurrence",
            description = "Met à jour une récurrence existante avec les nouvelles informations fournies"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Récurrence mise à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrenceDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Récurrence non trouvée", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<RecurrenceDto> updateRecurrence(
            @Parameter(description = "ID de la récurrence à mettre à jour", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations de la récurrence",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrenceDto.class))
            )
            @RequestBody RecurrenceDto recurrenceDto) {
        Recurrence updatedRecurrence = recurrenceService.update(recurrenceMapper.toEntity(recurrenceDto));
        return ResponseEntity.status(HttpStatus.OK).body(recurrenceMapper.toDto(updatedRecurrence));
    }

    @Operation(
            summary = "Supprimer une récurrence",
            description = "Supprime définitivement une récurrence existante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Récurrence supprimée avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Récurrence non trouvée", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurrence(
            @Parameter(description = "ID de la récurrence à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        Recurrence recurrence = recurrenceService.getById(id);
        recurrenceService.delete(recurrence);
        return ResponseEntity.noContent().build();
    }
}
