package com.team.backend.repository;

import com.team.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);

  @Query("""
    SELECT DISTINCT u
    FROM User u
    LEFT JOIN FETCH u.roles
    WHERE u.id = :id
    """)
  Optional<User> findByIdWithRoles(UUID id);
}
