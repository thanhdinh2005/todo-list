package com.team.backend.usecase.admin;

import com.team.backend.dto.request.auditlog.AuditLogFilterParam;
import com.team.backend.dto.response.AuditLogResponse;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.entity.AuditLog;
import com.team.backend.repository.AuditLogRepository;
import com.team.backend.specification.AuditLogSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminGetAuditLogsUseCase {
  private final AuditLogRepository auditLogRepository;

  public PageResponse<AuditLogResponse> execute(AuditLogFilterParam filter) {
    Specification<AuditLog> spec = AuditLogSpecification.buildFilter(
      filter.getEntityName(),
      filter.getEntityId(),
      filter.getAction(),
      filter.getFromDate(),
      filter.getToDate()
    );

    Page<AuditLog> page = auditLogRepository.findAll(spec, filter.toPageable());

    List<AuditLogResponse> items = page.getContent()
      .stream()
      .map(AuditLogResponse::from)
      .toList();

    return PageResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
  }
}
