package com.brieuc.cashtag.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;
import com.brieuc.cashtag.service.ComputationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Computation", description = "API de calcul et conversion de devises")
@RestController
@RequestMapping(value = "/computation", produces = "application/json")
@RequiredArgsConstructor
public class ComputationController {

      private final ComputationService computationService;

      @Operation(
              summary = "Calculer la somme avec conversion de devises",
              description = "Calcule la somme des montants fournis en appliquant les taux de change appropriés pour convertir dans la devise cible"
      )
      @ApiResponses(value = {
              @ApiResponse(
                      responseCode = "200",
                      description = "Calcul effectué avec succès",
                      content = @Content(
                              mediaType = "application/json",
                              schema = @Schema(implementation = ComputationResponseDto.class)
                      )
              ),
              @ApiResponse(
                      responseCode = "400",
                      description = "Données invalides ou taux de change manquant",
                      content = @Content
              )
      })
      @PostMapping(consumes = "application/json")
      public ResponseEntity<ComputationResponseDto> compute(
              @io.swagger.v3.oas.annotations.parameters.RequestBody(
                      description = "Requête de calcul contenant les montants et la devise cible",
                      required = true,
                      content = @Content(
                              mediaType = "application/json",
                              schema = @Schema(implementation = ComputationRequestDto.class)
                      )
              )
              @RequestBody ComputationRequestDto computationRequestDto) {
            ComputationResponseDto computationResponseDto = computationService.computeSum(computationRequestDto);
            return ResponseEntity.ok(computationResponseDto);
      }
}
