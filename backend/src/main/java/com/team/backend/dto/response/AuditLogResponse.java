package com.team.backend.dto.response;

import com.team.backend.entity.AuditLog;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class AuditLogResponse {
  private UUID id;
  private String entityName;
  private String entityId;
  private String action;
  private String performedBy;
  private Instant performedAt;
  private String oldValue;
  private String newValue;

  public static AuditLogResponse from(AuditLog log) {
    return AuditLogResponse.builder()
      .id(log.getId())
      .entityName(log.getEntityName())
      .entityId(log.getEntityId())
      .action(log.getAction().name())
      .performedBy(log.getPerformedBy())
      .performedAt(log.getPerformedAt())
      .oldValue(log.getOldValue())
      .newValue(log.getNewValue())
      .build();
  }
}
