package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.Entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long>, JpaSpecificationExecutor<Entry> {

    Page<Entry> findAll(Specification<Entry> specification, Pageable pageable);
    /*
    List<Entry> findByAccountingDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM Entry e JOIN e.tags t WHERE t.id = :tagId")
    List<Entry> findByTagId(@Param("tagId") Long tagId);

    List<Entry> findByCurrencyCode(String currencyCode);
     */
}