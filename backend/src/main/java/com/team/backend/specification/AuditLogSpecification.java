package com.team.backend.specification;

import com.team.backend.entity.AuditLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AuditLogSpecification {

  private AuditLogSpecification() {}

  public static Specification<AuditLog> entityNameEquals(String entityName) {
    if (entityName == null || entityName.isBlank()) {
      return null;
    }
    return (root, query, cb) -> cb.equal(root.get("entityName"), entityName);
  }

  public static Specification<AuditLog> entityIdEquals(String entityId) {
    if (entityId == null || entityId.isBlank()) {
      return null;
    }
    return (root, query, cb) -> cb.equal(root.get("entityId"), entityId);
  }

  public static Specification<AuditLog> actionEquals(AuditLog.Action action) {
    if (action == null) {
      return null;
    }
    return (root, query, cb) -> cb.equal(root.get("action"), action);
  }

  public static Specification<AuditLog> performedAfter(Instant fromDate) {
    if (fromDate == null) {
      return null;
    }
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("performedAt"), fromDate);
  }

  public static Specification<AuditLog> performedBefore(Instant toDate) {
    if (toDate == null) {
      return null;
    }
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("performedAt"), toDate);
  }

  public static Specification<AuditLog> buildFilter(
    String entityName,
    String entityId,
    AuditLog.Action action,
    Instant fromDate,
    Instant toDate
  ) {
    List<Specification<AuditLog>> specs = new ArrayList<>();

    addIfPresent(specs, entityNameEquals(entityName));
    addIfPresent(specs, entityIdEquals(entityId));
    addIfPresent(specs, actionEquals(action));
    addIfPresent(specs, performedAfter(fromDate));
    addIfPresent(specs, performedBefore(toDate));

    return specs.stream().reduce(Specification::and).orElse(null);
  }

  private static void addIfPresent(List<Specification<AuditLog>> specs, Specification<AuditLog> spec) {
    if (spec != null) {
      specs.add(spec);
    }
  }
}
