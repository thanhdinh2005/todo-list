package com.team.backend.usecase.task;

import com.team.backend.dto.response.TaskResponse;
import com.team.backend.entity.Task;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetTaskByIdUseCase {
  private final TaskRepository taskRepository;

  public TaskResponse execute(UUID currentUserId, UUID taskId) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> {
        log.warn("Task not found with id: {}", taskId);
        return new AppException(ErrorCode.NOT_FOUND, "Task not found with id: " + taskId);
      });

    if (!task.isOwnedBy(currentUserId)) {
      log.warn("Permission denied with uid: {} and taskId: {}", currentUserId, taskId);
      throw new AppException(
        ErrorCode.FORBIDDEN,
        "You are not permit to access this resource"
      );
    }

    return TaskResponse.from(task);
  }
}
