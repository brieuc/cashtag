package com.brieuc.cashtag.controller.api;

import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Computation", description = "API de calcul et conversion de devises")
@RequestMapping(value = "/computation", produces = "application/json")
public interface ComputationApi {

    @Operation(
            summary = "Calculer la somme avec conversion de devises",
            description = "Calcule la somme des montants fournis en appliquant les taux de change appropriés pour convertir dans la devise cible"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calcul effectué avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComputationResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou taux de change manquant", content = @Content)
    })
    @RequestBody(description = "Requête de calcul contenant les montants et la devise cible", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComputationRequestDto.class)))
    @PostMapping(consumes = "application/json")
    ResponseEntity<ComputationResponseDto> compute(ComputationRequestDto computationRequestDto);
}
