package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.repository.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntryService {

    private final EntryRepository entryRepository;

    public List<Entry> findAll() {
        return entryRepository.findAll();
    }

    public Optional<Entry> findById(Long id) {
        return entryRepository.findById(id);
    }

    public List<Entry> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return entryRepository.findByAccountingDateBetween(startDate, endDate);
    }

    public List<Entry> findByTag(Long tagId) {
        return entryRepository.findByTagId(tagId);
    }

    public List<Entry> findByCurrency(String currencyCode) {
        return entryRepository.findByCurrencyCode(currencyCode);
    }

    public Entry save(Entry entry) {
        return entryRepository.save(entry);
    }

    public void deleteById(Long id) {
        entryRepository.deleteById(id);
    }
}