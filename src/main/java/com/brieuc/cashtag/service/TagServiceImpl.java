package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.TagRepository;
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
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public Page<Tag> getTags(@NotNull Specification<Tag> specification, @NotNull Pageable pageable) {
        return tagRepository.findAll(specification, pageable);
    }

    @Override
    public Tag getById(@NotNull Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No tag id " + id + " was found"));
    }

    @Override
    public Tag create(@NotNull Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public void deleteById(@NotNull Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    public Tag update(@NotNull Tag detachedTag) {
        Tag tag = getById(detachedTag.getId());
        tag.setTitle(detachedTag.getTitle());
        tag.setDescription(detachedTag.getDescription());
        tag.setIcon(detachedTag.getIcon());
        tag.setSortingOrder(detachedTag.getSortingOrder());
        return tagRepository.save(tag);
    }


}