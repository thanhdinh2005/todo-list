package com.team.backend.controller;

import com.team.backend.apispec.TaskApiSpec;
import com.team.backend.common.AppResponse;
import com.team.backend.common.RateLimit;
import com.team.backend.dto.request.task.CreateTaskRequest;
import com.team.backend.dto.request.task.TaskFilterParam;
import com.team.backend.dto.request.task.UpdateTaskRequest;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.dto.response.TaskResponse;
import com.team.backend.dto.response.TaskStatsResponse;
import com.team.backend.security.CustomUserDetails;
import com.team.backend.usecase.task.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RateLimit
@RequiredArgsConstructor
public class TaskController implements TaskApiSpec {
  private final GetAllTasksUseCase getAllTasksUseCase;
  private final GetTaskByIdUseCase getTaskByIdUseCase;
  private final CreateTaskUseCase createTaskUseCase;
  private final UpdateTaskUseCase updateTaskUseCase;
  private final MarkAsCompleteTaskUseCase markAsCompleteTaskUseCase;
  private final GetAllOverdueTaskUseCase getAllOverdueTaskUseCase;
  private final TaskStatisticUseCase taskStatisticUseCase;

  @Override
  @GetMapping("/overdue")
  public ResponseEntity<PageResponse<TaskResponse>> getAllOverdueTasks(
    @ModelAttribute TaskFilterParam filter,
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    return ResponseEntity.ok(getAllOverdueTaskUseCase.execute(currentUser.getId(), filter));
  }

  @Override
  @GetMapping("/stats")
  public ResponseEntity<AppResponse<TaskStatsResponse>> taskStats(
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    return ResponseEntity.ok(AppResponse.success(taskStatisticUseCase.execute(currentUser.getId())));
  }

  @Override
  @GetMapping
  public ResponseEntity<PageResponse<TaskResponse>> getAllTasks(
    @ModelAttribute TaskFilterParam filter,
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    return ResponseEntity.ok(getAllTasksUseCase.execute(filter, currentUser.getId()));
  }

  @Override
  @PostMapping
  public ResponseEntity<AppResponse<TaskResponse>> createTask(
    @AuthenticationPrincipal CustomUserDetails currentUser,
    @RequestBody @Valid CreateTaskRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
      AppResponse.success(201, "Create successfully", createTaskUseCase.execute(currentUser.getId(), request))
    );
  }

  @Override
  @PatchMapping("/{taskId}/complete")
  public ResponseEntity<AppResponse<TaskResponse>> markAsCompleteTask(
    @AuthenticationPrincipal CustomUserDetails currentUser,
    @PathVariable UUID taskId
  ) {
    return ResponseEntity.ok(
      AppResponse.success(markAsCompleteTaskUseCase.execute(currentUser.getId(), taskId))
    );
  }

  @Override
  @PutMapping("/{taskId}")
  public ResponseEntity<AppResponse<TaskResponse>> updateTaskById(
    @AuthenticationPrincipal CustomUserDetails currentUser,
    @RequestBody @Valid UpdateTaskRequest request,
    @PathVariable UUID taskId
  ) {
    return ResponseEntity.ok(
      AppResponse.success(updateTaskUseCase.execute(currentUser.getId(), taskId, request))
    );
  }

  @Override
  @GetMapping("/{taskId}")
  public ResponseEntity<AppResponse<TaskResponse>> getTaskById(
    @PathVariable UUID taskId,
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    return ResponseEntity.ok(AppResponse.success(getTaskByIdUseCase.execute(currentUser.getId(), taskId)));
  }
}
