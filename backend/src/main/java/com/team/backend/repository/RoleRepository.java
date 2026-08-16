package com.team.backend.repository;

import com.team.backend.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
  Optional<Role> findByName(String name);
  boolean existsByName(String name);

  @Override
  @EntityGraph(attributePaths = "permissions")
  List<Role> findAll();

  @Override
  @EntityGraph(attributePaths = "permissions")
  Optional<Role> findById(UUID id);
}
