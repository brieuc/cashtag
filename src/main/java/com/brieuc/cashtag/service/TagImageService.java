package com.brieuc.cashtag.service;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.entity.TagImage;

@Service
public interface TagImageService {
      TagImage getTagImage(Tag tag);
}
