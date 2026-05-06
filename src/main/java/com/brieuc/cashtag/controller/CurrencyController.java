package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.controller.api.CurrencyApi;
import com.brieuc.cashtag.dto.CurrencyDto;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.mapper.CurrencyMapper;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.service.CurrencyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CurrencyController implements CurrencyApi {

    private final CurrencyServiceImpl currencyService;
    private final CurrencyMapper currencyMapper;
    private final PageRequestMapper pageRequestMapper;

    @Override
    public ResponseEntity<PageImpl<CurrencyDto>> getCurrencies(@ParameterObject PageRequestDto pageRequestDto) {
        Specification<Currency> specification = Specification.unrestricted();
        Page<CurrencyDto> currencies = currencyService.getCurrencies(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(currencyMapper::toDto);
        return ResponseEntity.ok(new PageImpl<>(currencies.getContent(), currencies.getPageable(), currencies.getTotalElements()));
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
