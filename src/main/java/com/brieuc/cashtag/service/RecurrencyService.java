package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Recurrency;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface RecurrencyService {
    Page<Recurrency> getRecurrencies(@NotNull Specification<Recurrency> specification, @NotNull Pageable pageable);
    Recurrency getById(@NotNull Long id);
    Recurrency create(@NotNull Recurrency recurrency);
    Recurrency update(@NotNull Recurrency recurrency);
    void delete(@NotNull Recurrency recurrency);
}
