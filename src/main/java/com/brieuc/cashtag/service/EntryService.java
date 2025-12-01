package com.brieuc.cashtag.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.brieuc.cashtag.entity.Entry;

import jakarta.validation.constraints.NotNull;

@Service
public interface EntryService {
      Page<Entry> getEntries(@NotNull Specification<Entry> specification, @NotNull Pageable pageable);
      Entry getById(@NotNull Long id);
      Entry create(@NotNull Entry entry);
      Entry update(@NotNull Entry entry);
      void delete(@NotNull Entry entry);
}
