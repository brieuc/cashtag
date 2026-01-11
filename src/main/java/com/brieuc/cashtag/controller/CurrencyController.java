package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.CurrencyDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.mapper.CurrencyMapper;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.service.CurrencyServiceImpl;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "Currencies", description = "API de gestion des devises")
@RestController
@RequestMapping(value = "/currencies", produces = "application/json")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyServiceImpl currencyService;
    private final CurrencyMapper currencyMapper;
    private final PageRequestMapper pageRequestMapper;

    @Operation(summary = "Récupérer toutes les devises", description = "Retourne une liste paginée de toutes les devises")
    @GetMapping
    public ResponseEntity<PageImpl<CurrencyDto>> getCurrencies(
            @Parameter(description = "Paramètres de pagination") @ParameterObject PageRequestDto pageRequestDto) {

        Specification<Currency> specification = Specification.unrestricted();
        Page<CurrencyDto> currencies = currencyService.getCurrencies(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(currencyMapper::toDto);

        return ResponseEntity.ok(new PageImpl<>(currencies.getContent(), currencies.getPageable(), currencies.getTotalElements()));
    }

    @Operation(summary = "Récupérer une devise par son code", description = "Retourne une devise spécifique identifiée par son code ISO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devise trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyDto.class))),
            @ApiResponse(responseCode = "404", description = "Devise non trouvée", content = @Content)
    })
    @GetMapping("/{code}")
    public ResponseEntity<CurrencyDto> getCurrencyByCode(
            @Parameter(description = "Code ISO de la devise", required = true, example = "EUR") @PathVariable String code) {
        return ResponseEntity.ok(currencyMapper.toDto(currencyService.getById(code)));
    }

    @Operation(summary = "Créer une nouvelle devise", description = "Crée une nouvelle devise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Devise créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<CurrencyDto> createCurrency(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Informations de la devise à créer", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyDto.class)))
            @RequestBody CurrencyDto currencyDto) {
        Currency saved = currencyService.create(currencyMapper.toEntity(currencyDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(currencyMapper.toDto(saved));
    }

    /*
    @PutMapping(value = "/{code}", consumes = "application/json")
    public ResponseEntity<CurrencyDto> updateCurrency(@PathVariable String code, @RequestBody CurrencyDto currencyDto) {
        if (!currencyDto.getCode().equals(code))
            throw new RuntimeException("no currency corresponding to this code");
        Currency currency = currencyService.update(currencyMapper.toEntity(currencyDto));
        return ResponseEntity.ok(currencyMapper.toDto(currency));
    }
    */

    @Operation(summary = "Supprimer une devise", description = "Supprime une devise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Devise supprimée avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Devise non trouvée", content = @Content)
    })
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCurrency(
            @Parameter(description = "Code ISO de la devise à supprimer", required = true, example = "EUR") @PathVariable String code) {
        currencyService.deleteById(code);
        return ResponseEntity.noContent().build();
    }
}
