package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.RateDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.service.CurrencyService;
import com.brieuc.cashtag.service.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rates")
@RequiredArgsConstructor
public class RateController {

    private final RateService rateService;
    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<List<RateDto>> getAllRates() {
        List<RateDto> rates = rateService.findAll().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RateDto> getRateById(@PathVariable Long id) {
        return rateService.findById(id)
                .map(r -> ResponseEntity.ok(toDto(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/currency/{currencyCode}")
    public ResponseEntity<List<RateDto>> getRatesByCurrency(@PathVariable String currencyCode) {
        List<RateDto> rates = rateService.findByCurrency(currencyCode).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(rates);
    }

    @PostMapping
    public ResponseEntity<RateDto> createRate(@RequestBody RateDto dto) {
        Currency currency = currencyService.findByCode(dto.getCurrencyCode())
                .orElseThrow(() -> new IllegalArgumentException("Currency not found"));

        Rate rate = new Rate(null, currency, dto.getValueDate(), dto.getRatePercent());
        Rate saved = rateService.save(rate);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RateDto> updateRate(@PathVariable Long id, @RequestBody RateDto dto) {
        return rateService.findById(id)
                .map(existing -> {
                    Currency currency = currencyService.findByCode(dto.getCurrencyCode())
                            .orElseThrow(() -> new IllegalArgumentException("Currency not found"));
                    existing.setCurrency(currency);
                    existing.setValueDate(dto.getValueDate());
                    existing.setRatePercent(dto.getRatePercent());
                    Rate updated = rateService.save(existing);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRate(@PathVariable Long id) {
        if (rateService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        rateService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private RateDto toDto(Rate rate) {
        return new RateDto(rate.getId(), rate.getCurrency().getCode(),
                          rate.getValueDate(), rate.getRatePercent());
    }
}