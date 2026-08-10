package com.team.backend.dto.response;

import java.time.Instant;
import java.util.UUID;

public class AuditLogResponse {
  private UUID id;
  private String entityName;
  private UUID entityId;
  private String action;
  private String performedBy;
  private Instant performedAt;
  private String oldValue;
  private String newValue;
}
