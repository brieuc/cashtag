package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.RateDto;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.RateMapper;
import com.brieuc.cashtag.service.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rates", produces = "application/json")
@RequiredArgsConstructor
public class RateController {

    private final RateService rateService;
    private final PageRequestMapper pageRequestMapper;
    private final RateMapper rateMapper;

    @GetMapping
    public ResponseEntity<Page<RateDto>> getAllRates(@ModelAttribute PageRequestDto pageRequestDto) {
        Specification<Rate> specification = Specification.unrestricted();
        Page<RateDto> rates = rateService.getRates(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(rateMapper::toDto);
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RateDto> getRateById(@PathVariable Long id) {
        return ResponseEntity.ok(rateMapper.toDto(rateService.getById(id)));
    }

    @GetMapping("/currency/{currencyCode}")
    public ResponseEntity<Page<RateDto>> getRatesByCurrency(@PathVariable String currencyCode, @ModelAttribute PageRequestDto pageRequestDto) {
        // TODO: Implement specification with currency filter
        Specification<Rate> specification = Specification.unrestricted();
        Page<RateDto> rates = rateService.getRates(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(rateMapper::toDto);
        return ResponseEntity.ok(rates);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<RateDto> createRate(@RequestBody RateDto rateDto) {
        Rate newRate = rateService.save(rateMapper.toEntity(rateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(rateMapper.toDto(newRate));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<RateDto> updateRate(@PathVariable Long id, @RequestBody RateDto rateDto) {
        if (!id.equals(rateDto.getId())) {
            throw new RuntimeException("no rate corresponding to this id");
        }
        Rate updatedRate = rateService.save(rateMapper.toEntity(rateDto));
        return ResponseEntity.status(HttpStatus.OK).body(rateMapper.toDto(updatedRate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRate(@PathVariable Long id) {
        Rate rate = rateService.getById(id);
        rateService.delete(rate);
        return ResponseEntity.noContent().build();
    }
}