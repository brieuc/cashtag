package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.controller.api.RateApi;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.RateDto;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.RateMapper;
import com.brieuc.cashtag.service.RateService;
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
public class RateController implements RateApi {

    private final RateService rateService;
    private final PageRequestMapper pageRequestMapper;
    private final RateMapper rateMapper;

    @Override
    public ResponseEntity<PageImpl<RateDto>> getRates(@ParameterObject PageRequestDto pageRequestDto) {
        Specification<Rate> specification = Specification.unrestricted();
        Page<RateDto> rates = rateService.getRates(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(rateMapper::toDto);
        return ResponseEntity.ok(new PageImpl<>(rates.getContent(), rates.getPageable(), rates.getTotalElements()));
    }

    @Override
    public ResponseEntity<RateDto> getRateById(@PathVariable Long id) {
        return ResponseEntity.ok(rateMapper.toDto(rateService.getById(id)));
    }

    @Override
    public ResponseEntity<PageImpl<RateDto>> getRatesByCurrency(@PathVariable String currencyCode, @ParameterObject PageRequestDto pageRequestDto) {
        // TODO: Implement specification with currency filter
        Specification<Rate> specification = Specification.unrestricted();
        Page<RateDto> rates = rateService.getRates(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(rateMapper::toDto);
        return ResponseEntity.ok(new PageImpl<>(rates.getContent(), rates.getPageable(), rates.getTotalElements()));
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
