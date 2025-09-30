package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.CurrencyDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<List<CurrencyDto>> getAllCurrencies() {
        List<CurrencyDto> currencies = currencyService.findAll().stream()
                .map(c -> new CurrencyDto(c.getCode()))
                .toList();
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/{code}")
    public ResponseEntity<CurrencyDto> getCurrencyByCode(@PathVariable String code) {
        return currencyService.findByCode(code)
                .map(c -> ResponseEntity.ok(new CurrencyDto(c.getCode())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CurrencyDto> createCurrency(@RequestBody CurrencyDto dto) {
        Currency currency = new Currency(dto.getCode());
        Currency saved = currencyService.save(currency);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CurrencyDto(saved.getCode()));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable String code) {
        if (currencyService.findByCode(code).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        currencyService.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }
}