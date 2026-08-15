package com.team.backend.specification;

import com.team.backend.entity.Task;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskSpecification {
  private TaskSpecification() {}

  public static Specification<Task> ownedBy(UUID ownerId) {
    return (
      root,
      query,
      cb
    ) -> cb.equal(root.get("owner").get("id"), ownerId);
  }

  public static Specification<Task> completedEquals(Boolean completed) {
    if (completed == null) {
      return null;
    }
    return (
      root,
      query,
      cb
    ) -> cb.equal(root.get("completed"), completed);
  }

  public static Specification<Task> categoryIdEquals(UUID categoryId) {
    if (categoryId == null) {
      return null;
    }
    return (
      root,
      query,
      cb
    ) -> cb.equal(root.get("category").get("id"), categoryId);
  }

  public static Specification<Task> dueDateAfter(Instant dueAfter) {
    if (dueAfter == null) {
      return null;
    }
    return (
      root,
      query,
      cb
    ) -> cb.greaterThanOrEqualTo(root.get("dueDate"), dueAfter);
  }

  public static Specification<Task> dueDateBefore(Instant dueBefore) {
    if (dueBefore == null) {
      return null;
    }
    return (
      root,
      query,
      cb
    ) -> cb.lessThanOrEqualTo(root.get("dueDate"), dueBefore);
  }

  public static Specification<Task> keywordMatches(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return null;
    }
    String pattern = "%" + keyword.trim().toLowerCase() + "%";
    return (root, query, cb) -> {
      Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
      Predicate descriptionMatch = cb.like(cb.lower(root.get("description")), pattern);
      return cb.or(titleMatch, descriptionMatch);
    };
  }

  public static Specification<Task> buildFilter(
    UUID ownerId,
    Boolean completed,
    UUID categoryId,
    Instant dueAfter,
    Instant dueBefore,
    String keyword
  ) {
    List<Specification<Task>> specs = new ArrayList<>();

    specs.add(ownedBy(ownerId));

    addIfPresent(specs, completedEquals(completed));
    addIfPresent(specs, categoryIdEquals(categoryId));
    addIfPresent(specs, dueDateAfter(dueAfter));
    addIfPresent(specs, dueDateBefore(dueBefore));
    addIfPresent(specs, keywordMatches(keyword));

    return specs.stream().reduce(Specification::and).orElseThrow();
  }

  private static void addIfPresent(
    List<Specification<Task>> specs,
    Specification<Task> spec) {
    if (spec != null) {
      specs.add(spec);
    }
  }
}
