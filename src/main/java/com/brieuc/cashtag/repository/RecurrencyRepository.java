package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.Recurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurrencyRepository extends JpaRepository<Recurrency, Long>, JpaSpecificationExecutor<Recurrency> {
}
