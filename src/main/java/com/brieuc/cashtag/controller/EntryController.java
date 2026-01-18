package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.EntryDto;
import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.mapper.EntryMapper;
import com.brieuc.cashtag.mapper.EntrySpecificationMapper;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.TagMapper;
import com.brieuc.cashtag.service.CurrencyService;
import com.brieuc.cashtag.service.EntryService;
import com.brieuc.cashtag.service.EntryServiceImpl;
import com.brieuc.cashtag.service.TagService;
import com.brieuc.cashtag.service.TagServiceImpl;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Entries", description = "API de gestion des entrées financières")
@RestController
@RequestMapping(value = "/entries", produces = "application/json")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;
    private final PageRequestMapper pageRequestMapper;
    private final EntryMapper entryMapper;
    private final EntrySpecificationMapper entrySpecificationMapper;

    @Operation(
            summary = "Récupérer toutes les entrées",
            description = "Retourne une liste paginée d'entrées filtrées selon les critères de recherche fournis"
    )
    @GetMapping
    public ResponseEntity<PageImpl<EntryDto>> getEntries(
            @Parameter(description = "Critères de filtrage des entrées")
            @ParameterObject EntrySpecificationDto entrySpecificationDto,
            @Parameter(description = "Paramètres de pagination (page, size, sort)")
            @ParameterObject PageRequestDto pageRequestDto) {
        Specification<Entry> specification = entrySpecificationMapper.toEntity(entrySpecificationDto);
        Page<EntryDto> entries = entryService.getEntries(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(entryMapper::tDto);
        return ResponseEntity.ok(new PageImpl<>(entries.getContent(),
                                                entries.getPageable(),
                                                entries.getTotalElements()));
    }

    @Operation(
            summary = "Récupérer une entrée par son ID",
            description = "Retourne une entrée spécifique identifiée par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Entrée trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntryDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entrée non trouvée",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntryDto> getEntryById(
            @Parameter(description = "ID de l'entrée à récupérer", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(entryMapper.tDto(entryService.getById(id)));

        /*
        return entryService.findById(id)
                .map(e -> ResponseEntity.ok(toDto(e)))
                .orElse(ResponseEntity.notFound().build());
        */
    }
/*
    @GetMapping("/by-date-range")
    public ResponseEntity<List<EntryDto>> getEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<EntryDto> entries = entryService.findByDateRange(startDate, endDate).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/by-tag/{tagId}")
    public ResponseEntity<List<EntryDto>> getEntriesByTag(@PathVariable Long tagId) {
        List<EntryDto> entries = entryService.findByTag(tagId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/by-currency/{currencyCode}")
    public ResponseEntity<List<EntryDto>> getEntriesByCurrency(@PathVariable String currencyCode) {
        List<EntryDto> entries = entryService.getByCurrency(currencyCode).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(entries);
    }
         */

    @Operation(
            summary = "Créer une nouvelle entrée",
            description = "Crée une nouvelle entrée financière avec les informations fournies"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Entrée créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntryDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides",
                    content = @Content
            )
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<EntryDto> createEntry(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations de l'entrée à créer",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntryDto.class)
                    )
            )
            @RequestBody EntryDto entryDto) {
        Entry newEntry = entryService.create(entryMapper.toEntity(entryDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(entryMapper.tDto(newEntry));
    }

    @Operation(
            summary = "Mettre à jour une entrée",
            description = "Met à jour une entrée existante avec les nouvelles informations fournies"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Entrée mise à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntryDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides ou incohérence entre l'ID du path et l'ID du body",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entrée non trouvée",
                    content = @Content
            )
    })
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<EntryDto> updateEntry(
            @Parameter(description = "ID de l'entrée à mettre à jour", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations de l'entrée",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntryDto.class)
                    )
            )
            @RequestBody EntryDto entryDto) {

        /* TODO: à voir si on remet ça plus tard car pour l'instant entryDto ne contient pas l'id
        car cela vient du formData renvoyé sans id par le modal.
        if(!id.equals(entryDto.getId()))
                throw new RuntimeException("no tag corresponding to this id");
        */
        Entry updatedEntry = entryService.update(entryMapper.toEntity(entryDto));
        return ResponseEntity.status(HttpStatus.OK).body(entryMapper.tDto(updatedEntry));
    }

    @Operation(
            summary = "Supprimer une entrée",
            description = "Supprime définitivement une entrée existante"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Entrée supprimée avec succès",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entrée non trouvée",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(
            @Parameter(description = "ID de l'entrée à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        Entry entry = entryService.getById(id);
        entryService.delete(entry);
        return ResponseEntity.noContent().build();
    }

}