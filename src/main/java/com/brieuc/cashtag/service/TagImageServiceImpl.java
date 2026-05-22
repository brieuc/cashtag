package com.brieuc.cashtag.service;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.entity.TagImage;
import com.brieuc.cashtag.repository.TagImageRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TagImageServiceImpl implements TagImageService {

      private final TagImageRepository tagImageRepository;

      @Override
      public TagImage getTagImage(Tag tag) {
            return tagImageRepository.findByTag(tag).orElse(null);
      }
      
}
