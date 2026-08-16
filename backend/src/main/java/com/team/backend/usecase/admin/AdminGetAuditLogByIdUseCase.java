package com.team.backend.usecase.admin;

import com.team.backend.dto.response.AuditLogResponse;
import com.team.backend.entity.AuditLog;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminGetAuditLogByIdUseCase {
  private final AuditLogRepository auditLogRepository;

  public AuditLogResponse execute(UUID id) {
    AuditLog log = auditLogRepository.findById(id)
      .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Audit log not found"));
    return AuditLogResponse.from(log);
  }
}
