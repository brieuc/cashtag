package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.controller.api.RateApi;
import com.brieuc.cashtag.dto.RateDto;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.mapper.RateMapper;
import com.brieuc.cashtag.service.CurrencyService;
import com.brieuc.cashtag.service.RateService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RateController implements RateApi {

    private final RateService rateService;
    private final CurrencyService currencyService;
    private final RateMapper rateMapper;

    @Override
    public ResponseEntity<RateDto> getRateById(@PathVariable Long id) {
        return ResponseEntity.ok(rateMapper.toDto(rateService.getById(id)));
    }

    @Override
    public ResponseEntity<List<RateDto>> getRatesByCurrency(@PathVariable String currencyCode) {
        String referenceCurrencyCode = currencyService.getReferenceCurrency().getCode(); 
        List<RateDto> rates = rateService.getRatesBySourceCurrencyAndTargetCurrency(currencyCode, referenceCurrencyCode).stream()
                .map(rateMapper::toDto).toList();
        return ResponseEntity.ok(rates);
    }

    @Override
    public ResponseEntity<RateDto> createRate(@RequestBody RateDto rateDto) {
        Rate newRate = rateService.save(rateMapper.toEntity(rateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(rateMapper.toDto(newRate));
    }

    @Override
    public ResponseEntity<RateDto> updateRate(@PathVariable Long id, @RequestBody RateDto rateDto) {
        if (!id.equals(rateDto.getId()))
            throw new RuntimeException("no rate corresponding to this id");
        return ResponseEntity.status(HttpStatus.OK).body(rateMapper.toDto(rateService.save(rateMapper.toEntity(rateDto))));
    }

    @Override
    public ResponseEntity<Void> deleteRate(@PathVariable Long id) {
        rateService.delete(rateService.getById(id));
        return ResponseEntity.noContent().build();
    }
}
