package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.Currency;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String>, JpaSpecificationExecutor<Currency> {

      Optional<Currency> findByCode(String code);
}