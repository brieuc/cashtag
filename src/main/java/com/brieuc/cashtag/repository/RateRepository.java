package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.entity.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long>, JpaSpecificationExecutor<Rate> {
    Page<Rate> findAll(Specification<Rate> specfication, Pageable pageable);
    List<Rate> findByCurrencyCode(String currencyCode);
    Optional<Rate> findByCurrencyCodeAndValueDate(String currencyCode, LocalDate valueDate);
}