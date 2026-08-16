package com.team.backend.dto.response;

import com.team.backend.entity.Task;

import java.time.Instant;
import java.util.UUID;

public record TaskSnapshot(String title, boolean completed, Instant dueDate, UUID categoryId) {
  public static TaskSnapshot from(Task task) {
    return new TaskSnapshot(
      task.getTitle(),
      task.isCompleted(),
      task.getDueDate(),
      task.getCategory() != null ? task.getCategory().getId() : null
    );
  }
}
