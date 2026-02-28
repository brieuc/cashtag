package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.RecurrencyDto;
import com.brieuc.cashtag.dto.RecurrencySpecificationDto;
import com.brieuc.cashtag.entity.Recurrency;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.RecurrencyMapper;
import com.brieuc.cashtag.mapper.RecurrencySpecificationMapper;
import com.brieuc.cashtag.service.RecurrencyService;
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

@Tag(name = "Recurrencies", description = "API de gestion des transactions récurrentes")
@RestController
@RequestMapping(value = "/recurrencies", produces = "application/json")
@RequiredArgsConstructor
public class RecurrencyController {

    private final RecurrencyService recurrencyService;
    private final PageRequestMapper pageRequestMapper;
    private final RecurrencyMapper recurrencyMapper;
    private final RecurrencySpecificationMapper recurrencySpecificationMapper;

    @Operation(
            summary = "Récupérer toutes les récurrences",
            description = "Retourne une liste paginée de récurrences filtrées selon les critères fournis"
    )
    @GetMapping
    public ResponseEntity<PageImpl<RecurrencyDto>> getRecurrencies(
            @Parameter(description = "Critères de filtrage des récurrences")
            @ParameterObject RecurrencySpecificationDto recurrencySpecificationDto,
            @Parameter(description = "Paramètres de pagination (page, size, sort)")
            @ParameterObject PageRequestDto pageRequestDto) {
        Specification<Recurrency> specification = recurrencySpecificationMapper.toEntity(recurrencySpecificationDto);
        Page<RecurrencyDto> recurrencies = recurrencyService.getRecurrencies(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(recurrencyMapper::toDto);
        return ResponseEntity.ok(new PageImpl<>(recurrencies.getContent(),
                                               recurrencies.getPageable(),
                                               recurrencies.getTotalElements()));
    }

    @Operation(
            summary = "Récupérer une récurrence par son ID",
            description = "Retourne une récurrence spécifique identifiée par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Récurrence trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrencyDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Récurrence non trouvée", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecurrencyDto> getRecurrencyById(
            @Parameter(description = "ID de la récurrence à récupérer", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(recurrencyMapper.toDto(recurrencyService.getById(id)));
    }

    @Operation(
            summary = "Créer une nouvelle récurrence",
            description = "Crée une nouvelle transaction récurrente avec les informations fournies"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Récurrence créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrencyDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<RecurrencyDto> createRecurrency(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations de la récurrence à créer",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrencyDto.class))
            )
            @RequestBody RecurrencyDto recurrencyDto) {
        Recurrency newRecurrency = recurrencyService.create(recurrencyMapper.toEntity(recurrencyDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(recurrencyMapper.toDto(newRecurrency));
    }

    @Operation(
            summary = "Mettre à jour une récurrence",
            description = "Met à jour une récurrence existante avec les nouvelles informations fournies"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Récurrence mise à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrencyDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Récurrence non trouvée", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<RecurrencyDto> updateRecurrency(
            @Parameter(description = "ID de la récurrence à mettre à jour", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations de la récurrence",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecurrencyDto.class))
            )
            @RequestBody RecurrencyDto recurrencyDto) {
        Recurrency updatedRecurrency = recurrencyService.update(recurrencyMapper.toEntity(recurrencyDto));
        return ResponseEntity.status(HttpStatus.OK).body(recurrencyMapper.toDto(updatedRecurrency));
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
    public ResponseEntity<Void> deleteRecurrency(
            @Parameter(description = "ID de la récurrence à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        Recurrency recurrency = recurrencyService.getById(id);
        recurrencyService.delete(recurrency);
        return ResponseEntity.noContent().build();
    }
}
