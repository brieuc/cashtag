package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.CurrencyDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.mapper.CurrencyMapper;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.service.CurrencyServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/currencies", produces = "application/json")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyServiceImpl currencyService;
    private final CurrencyMapper currencyMapper;
    private final PageRequestMapper pageRequestMapper;

    @GetMapping
    public ResponseEntity<Page<CurrencyDto>> getAllCurrencies(@ModelAttribute PageRequestDto pageRequestDto) {

        Specification<Currency> specification = Specification.unrestricted();
        Page<CurrencyDto> currencies = currencyService.getCurrencies(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(currencyMapper::toDto);

        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/{code}")
    public ResponseEntity<CurrencyDto> getCurrencyByCode(@PathVariable String code) {
        return ResponseEntity.ok(currencyMapper.toDto(currencyService.getById(code)));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<CurrencyDto> createCurrency(@RequestBody CurrencyDto currencyDto) {
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

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable String code) {
        currencyService.deleteById(code);
        return ResponseEntity.noContent().build();
    }
}