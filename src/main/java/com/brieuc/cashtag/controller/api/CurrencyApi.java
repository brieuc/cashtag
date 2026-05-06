package com.brieuc.cashtag.controller.api;

import com.brieuc.cashtag.dto.CurrencyDto;
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

@Tag(name = "Currencies", description = "API de gestion des devises")
@RequestMapping(value = "/currencies", produces = "application/json")
public interface CurrencyApi {

    @Operation(summary = "Récupérer toutes les devises", description = "Retourne une liste paginée de toutes les devises")
    @GetMapping
    ResponseEntity<PageImpl<CurrencyDto>> getCurrencies(
            @Parameter(description = "Paramètres de pagination") @ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "Récupérer une devise par son code", description = "Retourne une devise spécifique identifiée par son code ISO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devise trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyDto.class))),
            @ApiResponse(responseCode = "404", description = "Devise non trouvée", content = @Content)
    })
    @GetMapping("/{code}")
    ResponseEntity<CurrencyDto> getCurrencyByCode(
            @Parameter(description = "Code ISO de la devise", required = true, example = "EUR") @PathVariable String code);

    @Operation(summary = "Créer une nouvelle devise", description = "Crée une nouvelle devise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Devise créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @RequestBody(description = "Informations de la devise à créer", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyDto.class)))
    @PostMapping(consumes = "application/json")
    ResponseEntity<CurrencyDto> createCurrency(CurrencyDto currencyDto);

    @Operation(summary = "Supprimer une devise", description = "Supprime une devise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Devise supprimée avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Devise non trouvée", content = @Content)
    })
    @DeleteMapping("/{code}")
    ResponseEntity<Void> deleteCurrency(
            @Parameter(description = "Code ISO de la devise à supprimer", required = true, example = "EUR") @PathVariable String code);
}
