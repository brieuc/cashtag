package com.brieuc.cashtag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brieuc.cashtag.entity.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
      Optional<User> findByUsername(String username);
}
