package com.team.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.backend.entity.AuditLog;
import com.team.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {
  private final AuditLogRepository auditLogRepository;
  private final ObjectMapper objectMapper;

  public void logCreate(String entityName, UUID entityId, String performedBy, Object newValue) {
    AuditLog log = AuditLog.recordCreate(entityName, entityId.toString(), performedBy, toJson(newValue));
    auditLogRepository.save(log);
  }

  public void logUpdate(String entityName, UUID entityId, String performedBy, Object oldValue, Object newValue) {
    AuditLog log = AuditLog.recordUpdate(entityName, entityId.toString(), performedBy, toJson(oldValue), toJson(newValue));
    auditLogRepository.save(log);
  }

  public void logDelete(String entityName, UUID entityId, String performedBy, Object oldValue) {
    AuditLog log = AuditLog.recordDelete(entityName, entityId.toString(), performedBy, toJson(oldValue));
    auditLogRepository.save(log);
  }

  private String toJson(Object value) {
    if (value == null) return null;
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      return "{\"error\": \"serialization failed\"}";
    }
  }
}
