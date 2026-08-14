package com.team.backend.repository;

import com.team.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
  @Query("""
    SELECT c
    FROM Category c
    WHERE c.owner.id = :ownerId
  """)
  List<Category> findAllByOwnerId(@Param("ownerId") UUID ownerId);

  boolean existsByNameAndOwnerId(String name, UUID currentUserId);
}
