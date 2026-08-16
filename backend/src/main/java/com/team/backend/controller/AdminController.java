package com.team.backend.controller;

import com.team.backend.common.AppResponse;
import com.team.backend.dto.request.auditlog.AuditLogFilterParam;
import com.team.backend.dto.request.task.TaskFilterParam;
import com.team.backend.dto.response.AuditLogResponse;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.dto.response.TaskResponse;
import com.team.backend.usecase.admin.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final AdminGetAllTasksUseCase adminGetAllTasksUseCase;
  private final AdminGetAuditLogsUseCase adminGetAuditLogsUseCase;
  private final AdminGetAuditLogByIdUseCase adminGetAuditLogByIdUseCase;

  @GetMapping("/audit-logs")
  public ResponseEntity<AppResponse<PageResponse<AuditLogResponse>>> getAllAuditLogs(
    @ModelAttribute AuditLogFilterParam filter
  ) {
    var result = adminGetAuditLogsUseCase.execute(filter);
    return ResponseEntity.ok(
      AppResponse.success(200, "Get audit logs successfully", result)
    );
  }

  @GetMapping("/audit-logs/{id}")
  public ResponseEntity<AppResponse<AuditLogResponse>> getAuditLogById(
    @PathVariable UUID id
  ) {
    var result = adminGetAuditLogByIdUseCase.execute(id);
    return ResponseEntity.ok(
      AppResponse.success(200, "Get audit log successfully", result)
    );
  }

  @GetMapping("/tasks")
  public ResponseEntity<AppResponse<PageResponse<TaskResponse>>> getAllTasks(
    @ModelAttribute TaskFilterParam filter
  ) {
    var result = adminGetAllTasksUseCase.execute(filter);
    return ResponseEntity.ok(
      AppResponse.success(200, "Get all tasks successfully", result)
    );
  }
}
