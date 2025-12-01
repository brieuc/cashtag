package com.brieuc.cashtag.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.brieuc.cashtag.entity.Tag;

import jakarta.validation.constraints.NotNull;

public interface TagService {
      Page<Tag> getTags(@NotNull Specification<Tag> specification, @NotNull Pageable pageable);
      Tag getById(@NotNull Long id);
      Tag create(@NotNull Tag tag);
      Tag update(@NotNull Tag tag);
      void deleteById(@NotNull Long id);
      
}
