package com.brieuc.cashtag.controller;

import com.brieuc.cashtag.controller.api.TagApi;
import com.brieuc.cashtag.dto.PageRequestDto;
import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.mapper.PageRequestMapper;
import com.brieuc.cashtag.mapper.TagMapper;
import com.brieuc.cashtag.service.TagServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class TagController implements TagApi {

    private final TagServiceImpl tagService;
    private final TagMapper tagMapper;
    private final PageRequestMapper pageRequestMapper;

    @Override
    public ResponseEntity<PageImpl<TagDto>> getTags(@ParameterObject PageRequestDto pageRequestDto) {
        Specification<Tag> specification = Specification.unrestricted();
        Page<TagDto> tags = tagService.getTags(specification, pageRequestMapper.toPageable(pageRequestDto))
                .map(tagMapper::toDto);
        return ResponseEntity.ok(new PageImpl<>(tags.getContent(), tags.getPageable(), tags.getTotalElements()));
    }

    @Override
    public ResponseEntity<TagDto> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagMapper.toDto(tagService.getById(id)));
    }

    @Override
    public ResponseEntity<TagDto> createTag(@RequestBody TagDto tagDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagMapper.toDto(tagService.create(tagMapper.toEntity(tagDto))));
    }

    @Override
    public ResponseEntity<TagDto> uploadIcon(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(tagMapper.toDto(tagService.uploadIcon(id, file)));
    }

    @Override
    public ResponseEntity<TagDto> updateTag(@PathVariable Long id, @RequestBody TagDto tagDto) {
        if (!tagDto.getId().equals(id))
            throw new RuntimeException("no tag corresponding to this id " + id);
        return ResponseEntity.ok(tagMapper.toDto(tagService.update(tagMapper.toEntity(tagDto))));
    }

    @Override
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
