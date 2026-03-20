package com.brieuc.cashtag.repository;

import com.brieuc.cashtag.entity.Recurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurrenceRepository extends JpaRepository<Recurrence, Long>, JpaSpecificationExecutor<Recurrence> {
}
