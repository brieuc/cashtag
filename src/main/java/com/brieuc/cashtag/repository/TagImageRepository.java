package com.brieuc.cashtag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.entity.TagImage;

@Repository
public interface TagImageRepository extends JpaRepository<TagImage, Long> {

      Optional<TagImage> findByTag(Tag tag);
}
