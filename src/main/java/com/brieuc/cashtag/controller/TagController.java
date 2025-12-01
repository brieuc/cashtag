package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.TagMapper;
import com.brieuc.cashtag.service.TagServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/tags", produces = "application/json")
@RequiredArgsConstructor
public class TagController {

    private final TagServiceImpl tagService;
    private final TagMapper tagMapper;
    private final PageRequestMapper pageRequestMapper;

    @GetMapping
    public ResponseEntity<Page<TagDto>> getAllTags(@ModelAttribute PageRequestDto pageRequestDto) {
        
        Specification<Tag> specification = Specification.unrestricted();
        Page<TagDto> tags = tagService.getTags(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(tagMapper::toDto);
        
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagMapper.toDto(tagService.getById(id)));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<TagDto> createTag(@RequestBody TagDto tagDto) {
        Tag saved = tagService.create(tagMapper.toEntity(tagDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(tagMapper.toDto(saved));
    }

    @PutMapping(value = "/{id}",  consumes = "application/json")
    public ResponseEntity<TagDto> updateTag(@PathVariable Long id, @RequestBody TagDto tagDto) {
        if (!tagDto.getId().equals(id))
            throw new RuntimeException("no tag corresponding to this id " + id);
        Tag tag = tagService.update(tagMapper.toEntity(tagDto));
        return ResponseEntity.ok(tagMapper.toDto(tag));
        /*
        return tagService.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.getTitle());
                    existing.setDescription(dto.getDescription());
                    existing.setIcon(dto.getIcon());
                    Tag updated = tagService.save(existing);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
        */
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteById(id);
        return ResponseEntity.noContent().build();
        /*
        if (tagService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        tagService.deleteById(id);
        return ResponseEntity.noContent().build();
         */
    }
}