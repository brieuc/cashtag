package com.brieuc.cashtag.controller.api;

import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.RateDto;
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

@Tag(name = "Rates", description = "API de gestion des taux de change")
@RequestMapping(value = "/rates", produces = "application/json")
public interface RateApi {

    @Operation(summary = "Récupérer tous les taux de change", description = "Retourne une liste paginée de tous les taux de change")
    @GetMapping
    ResponseEntity<PageImpl<RateDto>> getRates(
            @Parameter(description = "Paramètres de pagination") @ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "Récupérer un taux par son ID", description = "Retourne un taux de change spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Taux trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateDto.class))),
            @ApiResponse(responseCode = "404", description = "Taux non trouvé", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<RateDto> getRateById(
            @Parameter(description = "ID du taux", required = true, example = "1") @PathVariable Long id);

    @Operation(summary = "Récupérer les taux par devise", description = "Retourne les taux de change pour une devise spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des taux pour la devise",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RateDto.class))))
    })
    @GetMapping("/currency/{currencyCode}")
    ResponseEntity<PageImpl<RateDto>> getRatesByCurrency(
            @Parameter(description = "Code ISO de la devise", required = true, example = "EUR") @PathVariable String currencyCode,
            @Parameter(description = "Paramètres de pagination") @ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "Créer un nouveau taux", description = "Crée un nouveau taux de change")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Taux créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @RequestBody(description = "Informations du taux à créer", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateDto.class)))
    @PostMapping(consumes = "application/json")
    ResponseEntity<RateDto> createRate(RateDto rateDto);

    @Operation(summary = "Mettre à jour un taux", description = "Met à jour un taux de change existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Taux mis à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Taux non trouvé", content = @Content)
    })
    @RequestBody(description = "Nouvelles informations du taux", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateDto.class)))
    @PutMapping(value = "/{id}", consumes = "application/json")
    ResponseEntity<RateDto> updateRate(
            @Parameter(description = "ID du taux à mettre à jour", required = true, example = "1") @PathVariable Long id,
            RateDto rateDto);

    @Operation(summary = "Supprimer un taux", description = "Supprime un taux de change")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Taux supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Taux non trouvé", content = @Content)
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteRate(
            @Parameter(description = "ID du taux à supprimer", required = true, example = "1") @PathVariable Long id);
}
