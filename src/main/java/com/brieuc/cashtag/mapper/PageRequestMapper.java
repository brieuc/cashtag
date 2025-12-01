package com.brieuc.cashtag.mapper;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.PageRequestDto;

@Service
public class PageRequestMapper {
    
    public Pageable toPageable(PageRequestDto dto) {
        int page = dto.getPage() != null ? dto.getPage() : 0;
        int size = dto.getSize() != null ? dto.getSize() : 20;
        
        if (dto.getSort() == null || dto.getSort().isEmpty()) {
            return PageRequest.of(page, size);
        }
        
        List<Sort.Order> orders = dto.getSort().stream()
            .map(s -> parseSort(s))
            .toList();
        
        return PageRequest.of(page, size, Sort.by(orders));
    }
    
    private Sort.Order parseSort(String sort) {
        String[] parts = sort.split(",");
        String property = parts[0];
        
        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1])) {
            return Sort.Order.desc(property);
        }
        
        return Sort.Order.asc(property);
    }
}