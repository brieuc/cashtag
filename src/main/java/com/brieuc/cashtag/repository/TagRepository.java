package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag>  {
    Optional<Tag> findByTitle(String title);
    Page<Tag> findAll(Specification<Tag> specfication, Pageable pageable);
}