package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.RateDto;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.RateMapper;
import com.brieuc.cashtag.service.RateService;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "Rates", description = "API de gestion des taux de change")
@RestController
@RequestMapping(value = "/rates", produces = "application/json")
@RequiredArgsConstructor
public class RateController {

    private final RateService rateService;
    private final PageRequestMapper pageRequestMapper;
    private final RateMapper rateMapper;

    @Operation(summary = "Récupérer tous les taux de change", description = "Retourne une liste paginée de tous les taux de change")
    @GetMapping
    public ResponseEntity<PageImpl<RateDto>> getRates(
            @Parameter(description = "Paramètres de pagination") @ParameterObject PageRequestDto pageRequestDto) {
        Specification<Rate> specification = Specification.unrestricted();
        Page<RateDto> rates = rateService.getRates(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(rateMapper::toDto);
        return ResponseEntity.ok(new PageImpl<>(rates.getContent(), rates.getPageable(), rates.getTotalElements()));
    }

    @Operation(summary = "Récupérer un taux par son ID", description = "Retourne un taux de change spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Taux trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateDto.class))),
            @ApiResponse(responseCode = "404", description = "Taux non trouvé", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RateDto> getRateById(
            @Parameter(description = "ID du taux", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(rateMapper.toDto(rateService.getById(id)));
    }

    @Operation(summary = "Récupérer les taux par devise", description = "Retourne les taux de change pour une devise spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des taux pour la devise",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RateDto.class))))
    })
    @GetMapping("/currency/{currencyCode}")
    public ResponseEntity<PageImpl<RateDto>> getRatesByCurrency(
            @Parameter(description = "Code ISO de la devise", required = true, example = "EUR") @PathVariable String currencyCode,
            @Parameter(description = "Paramètres de pagination") @ParameterObject PageRequestDto pageRequestDto) {
        // TODO: Implement specification with currency filter
        Specification<Rate> specification = Specification.unrestricted();
        Page<RateDto> rates = rateService.getRates(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(rateMapper::toDto);
        return ResponseEntity.ok(new PageImpl<>(rates.getContent(), rates.getPageable(), rates.getTotalElements()));
    }

    @Operation(summary = "Créer un nouveau taux", description = "Crée un nouveau taux de change")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Taux créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<RateDto> createRate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Informations du taux à créer", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateDto.class)))
            @RequestBody RateDto rateDto) {
        Rate newRate = rateService.save(rateMapper.toEntity(rateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(rateMapper.toDto(newRate));
    }

    @Operation(summary = "Mettre à jour un taux", description = "Met à jour un taux de change existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Taux mis à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Taux non trouvé", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<RateDto> updateRate(
            @Parameter(description = "ID du taux à mettre à jour", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nouvelles informations du taux", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateDto.class)))
            @RequestBody RateDto rateDto) {
        if (!id.equals(rateDto.getId())) {
            throw new RuntimeException("no rate corresponding to this id");
        }
        Rate updatedRate = rateService.save(rateMapper.toEntity(rateDto));
        return ResponseEntity.status(HttpStatus.OK).body(rateMapper.toDto(updatedRate));
    }

    @Operation(summary = "Supprimer un taux", description = "Supprime un taux de change")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Taux supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Taux non trouvé", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRate(
            @Parameter(description = "ID du taux à supprimer", required = true, example = "1") @PathVariable Long id) {
        Rate rate = rateService.getById(id);
        rateService.delete(rate);
        return ResponseEntity.noContent().build();
    }
}