package com.team.backend.repository;

import com.team.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
  @Modifying
  @Query("DELETE FROM Task t WHERE t.category.id = :categoryId")
  void deleteAllByCategoryId(@Param("categoryId") UUID categoryId);
}
