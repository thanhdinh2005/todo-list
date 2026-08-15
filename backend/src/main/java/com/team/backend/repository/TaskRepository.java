package com.team.backend.repository;

import com.team.backend.projection.TaskStatsProjection;
import com.team.backend.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
  @Override
  @EntityGraph(attributePaths = "category")
  Page<Task> findAll(Specification<Task> spec, Pageable pageable);

  @Query(value = """
    SELECT
        COUNT(*) AS total,
        SUM(CASE WHEN completed THEN 1 ELSE 0 END) AS completed,
        SUM(CASE WHEN NOT completed THEN 1 ELSE 0 END) AS pending,
        SUM(CASE WHEN NOT completed AND due_date < :now THEN 1 ELSE 0 END) AS overdue
    FROM tasks
    WHERE owner_id = :ownerId
    """, nativeQuery = true)
  TaskStatsProjection getStats(@Param("ownerId") UUID ownerId, @Param("now") Instant now);
}
