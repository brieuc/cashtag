package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    List<Rate> findByCurrencyCode(String currencyCode);
    Optional<Rate> findByCurrencyCodeAndValueDate(String currencyCode, LocalDate valueDate);
}