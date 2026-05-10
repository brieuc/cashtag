package com.brieuc.cashtag.service;

import java.util.List;

import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.EntryRepository;
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
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;

    @Override
    public Page<Entry> getEntries(@NotNull Specification<Entry> specification, @NotNull Pageable pageable) {
        return entryRepository.findAll(specification, pageable);
    }


    @Override
    public Entry getById(@NotNull Long id) {
        return entryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("no entry id " + id + " was found"));
    }

    /*
    @Override
    public List<Entry> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return entryRepository.findByAccountingDateBetween(startDate, endDate);
    }

    @Override
    public List<Entry> getByTag(Long tagId) {
        return entryRepository.findByTagId(tagId);
    }

    public List<Entry> getByCurrency(String currencyCode) {
        return entryRepository.findByCurrencyCode(currencyCode);
    }
    */

    @Override
    public Entry create(@NotNull Entry entry) {
        return entryRepository.save(entry);
    }

    @Override
    public List<Entry> createBatch(@NotNull List<Entry> entries) {
        return entryRepository.saveAll(entries);
    }

    @Override
    public Entry update(@NotNull Entry detachedEntry) {
        Entry entry = getById(detachedEntry.getId());
        entry.setAccountingDate(detachedEntry.getAccountingDate());
        entry.setTitle(detachedEntry.getTitle());
        entry.setDescription(detachedEntry.getDescription());
        entry.setAmount(detachedEntry.getAmount());
        entry.setCurrency(detachedEntry.getCurrency());
        entry.setTags(detachedEntry.getTags());
        return entryRepository.save(entry);
    }

    @Override
    public void delete(@NotNull Entry entry) {
        entryRepository.delete(entry);
    }
}