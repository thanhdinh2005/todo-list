package com.team.backend.usecase.task;

import com.team.backend.dto.request.task.CreateTaskRequest;
import com.team.backend.dto.response.TaskResponse;
import com.team.backend.entity.Category;
import com.team.backend.entity.Task;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.CategoryRepository;
import com.team.backend.repository.TaskRepository;
import com.team.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateTaskUseCase {
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  public TaskResponse execute(UUID currentUserId, CreateTaskRequest request) {
    User user = userRepository.findById(currentUserId)
      .orElseThrow(() -> {
        log.warn("User not found with id: {}", currentUserId);
        return new AppException(ErrorCode.NOT_FOUND, "User not found with id: " + currentUserId);
      });

    Category category = null;
    if (request.getCategoryId() != null) {
      category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> {
          log.warn("Category not found with id: {}", request.getCategoryId());
          return new AppException(ErrorCode.NOT_FOUND, "Category not found");
        });

      if (!category.isOwnedBy(currentUserId)) {
        log.warn("Category {} does not belong to user {}", request.getCategoryId(), currentUserId);
        throw new AppException(ErrorCode.FORBIDDEN, "Category does not belong to current user");
      }
    }

    Task task = taskRepository.save(
      Task.create(
        request.getTitle(),
        request.getDescription(),
        request.getDueDate(),
        user,
        category
      )
    );
    log.info("Created task successfully with id: {}", task.getId());

    return TaskResponse.from(task);
  }
}
