package com.team.backend.usecase.task;

import com.team.backend.dto.request.task.UpdateTaskRequest;
import com.team.backend.dto.response.TaskResponse;
import com.team.backend.entity.Category;
import com.team.backend.entity.Task;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.CategoryRepository;
import com.team.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateTaskUseCase {
  private final TaskRepository taskRepository;
  private final CategoryRepository categoryRepository;

  public TaskResponse execute(UUID currentUserId, UUID taskId, UpdateTaskRequest request) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> {
        log.warn("Task not found with id: {}", taskId);
        return new AppException(ErrorCode.NOT_FOUND, "Task not found");
      });

    if (!task.isOwnedBy(currentUserId)) {
      log.warn("Permission denied with uid: {} and taskId: {}", currentUserId, taskId);
      throw new AppException(ErrorCode.FORBIDDEN, "You are not permit to access this resource");
    }

    if (request.getTitle() != null && !request.getTitle().isBlank()) {
      task.updateTitle(request.getTitle());
    }

    if (request.getDescription() != null) {
      task.updateDescription(request.getDescription());
    }

    if (request.getDueDate() != null) {
      task.updateDueDate(request.getDueDate());
    }

    if (request.getCategoryId() != null) {
      Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Category not found"));

      if (!category.isOwnedBy(currentUserId)) {
        throw new AppException(ErrorCode.FORBIDDEN, "Category does not belong to current user");
      }

      task.assignCategory(category);
    }

    log.info("Updated task successfully with id: {}", task.getId());
    return TaskResponse.from(task);
  }
}
