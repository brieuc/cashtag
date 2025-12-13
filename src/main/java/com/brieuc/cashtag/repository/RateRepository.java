package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.entity.Tag;

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
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long>, JpaSpecificationExecutor<Rate> {
    Page<Rate> findAll(Specification<Rate> specification, Pageable pageable);
    List<Rate> findBySourceCurrencyCode(String sourceCurrencyCode);
    List<Rate> findByTargetCurrencyCode(String targetCurrencyCode);
    @Query("SELECT r FROM Rate r WHERE r.sourceCurrency.code = :source " +
        "AND r.targetCurrency.code = :target " +
        "AND r.valueDate <= :valueDate " +
        "ORDER BY r.valueDate DESC LIMIT 1")
    Optional<Rate> findClosestRateBefore(
        @Param("source") String sourceCurrencyCode,
        @Param("target") String targetCurrencyCode, 
        @Param("valueDate") LocalDate valueDate
    );
}