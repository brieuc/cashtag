package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {
    @Query("SELECT p FROM Period p WHERE :date BETWEEN CAST(p.startDate AS date) AND CAST(p.endDate AS date)")
    Optional<Period> findPeriodByDate(@Param("date") LocalDate date);
}