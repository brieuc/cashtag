package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Recurrency;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.RecurrencyRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class RecurrencyServiceImpl implements RecurrencyService {

    private final RecurrencyRepository recurrencyRepository;

    @Override
    public Page<Recurrency> getRecurrencies(@NotNull Specification<Recurrency> specification, @NotNull Pageable pageable) {
        return recurrencyRepository.findAll(specification, pageable);
    }

    @Override
    public Recurrency getById(@NotNull Long id) {
        return recurrencyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("no recurrency id " + id + " was found"));
    }

    @Override
    public Recurrency create(@NotNull Recurrency recurrency) {
        return recurrencyRepository.save(recurrency);
    }

    @Override
    public Recurrency update(@NotNull Recurrency detachedRecurrency) {
        Recurrency recurrency = getById(detachedRecurrency.getId());
        recurrency.setTitle(detachedRecurrency.getTitle());
        recurrency.setDescription(detachedRecurrency.getDescription());
        recurrency.setAmount(detachedRecurrency.getAmount());
        recurrency.setCurrency(detachedRecurrency.getCurrency());
        recurrency.setStartDate(detachedRecurrency.getStartDate());
        recurrency.setFrequency(detachedRecurrency.getFrequency());
        recurrency.setTags(detachedRecurrency.getTags());
        return recurrencyRepository.save(recurrency);
    }

    @Override
    public void delete(@NotNull Recurrency recurrency) {
        recurrencyRepository.delete(recurrency);
    }
}
