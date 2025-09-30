package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags() {
        List<TagDto> tags = tagService.findAll().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTagById(@PathVariable Long id) {
        return tagService.findById(id)
                .map(t -> ResponseEntity.ok(toDto(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TagDto> createTag(@RequestBody TagDto dto) {
        Tag tag = new Tag(null, dto.getTitle(), dto.getDescription(), dto.getIcon());
        Tag saved = tagService.save(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> updateTag(@PathVariable Long id, @RequestBody TagDto dto) {
        return tagService.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.getTitle());
                    existing.setDescription(dto.getDescription());
                    existing.setIcon(dto.getIcon());
                    Tag updated = tagService.save(existing);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        if (tagService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        tagService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private TagDto toDto(Tag tag) {
        return new TagDto(tag.getId(), tag.getTitle(), tag.getDescription(), tag.getIcon());
    }
}