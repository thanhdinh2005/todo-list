package com.team.backend.dto.request.auditlog;

import com.team.backend.dto.request.BasePageRequest;
import com.team.backend.entity.AuditLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLogFilterParam extends BasePageRequest {
  private static final Set<String> ALLOWED_SORT = Set.of("performedAt");

  private String entityName;
  private String entityId;
  private AuditLog.Action action;
  private Instant fromDate;
  private Instant toDate;

  @Override
  protected Set<String> getAllowedSortFields() {
    return ALLOWED_SORT;
  }
}
