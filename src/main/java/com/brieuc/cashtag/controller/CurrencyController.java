package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.controller.api.CurrencyApi;
import com.brieuc.cashtag.dto.CurrencyDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.mapper.CurrencyMapper;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.service.CurrencyService;
import com.brieuc.cashtag.service.CurrencyServiceImpl;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CurrencyController implements CurrencyApi {

    private final CurrencyService currencyService;
    private final CurrencyMapper currencyMapper;

    @Override
    public ResponseEntity<List<CurrencyDto>> getCurrencies() {
        List<CurrencyDto> currencies = currencyService.getCurrencies().stream().map(currencyMapper::toDto).toList();
        return ResponseEntity.ok(currencies);
    }

    @Override
    public ResponseEntity<CurrencyDto> getReferenceCurrency() {
        return ResponseEntity.ok(currencyMapper.toDto(currencyService.getReferenceCurrency()));
    }

    @Override
    public ResponseEntity<CurrencyDto> getCurrencyByCode(@PathVariable String code) {
        return ResponseEntity.ok(currencyMapper.toDto(currencyService.getById(code)));
    }

    @Override
    public ResponseEntity<CurrencyDto> createCurrency(@RequestBody CurrencyDto currencyDto) {
        Currency saved = currencyService.create(currencyMapper.toEntity(currencyDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(currencyMapper.toDto(saved));
    }

    @Override
    public ResponseEntity<Void> deleteCurrency(@PathVariable String code) {
        currencyService.deleteById(code);
        return ResponseEntity.noContent().build();
    }
}
