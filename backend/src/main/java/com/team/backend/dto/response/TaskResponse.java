package com.team.backend.dto.response;

import com.team.backend.entity.Task;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class TaskResponse {
  private UUID id;
  private String title;
  private String description;
  private boolean completed;
  private Instant dueDate;
  private CategorySummary category;
  private Instant createdAt;
  private Instant updatedAt;

  @Getter
  @Builder
  public static class CategorySummary {
    private UUID id;
    private String name;
    private String colorCode;
  }

  public static TaskResponse from(Task task) {
    return TaskResponse.builder()
      .id(task.getId())
      .title(task.getTitle())
      .description(task.getDescription())
      .completed(task.isCompleted())
      .dueDate(task.getDueDate())
      .category(task.getCategory() != null
        ? CategorySummary.builder()
        .id(task.getCategory().getId())
        .name(task.getCategory().getName())
        .colorCode(task.getCategory().getColorCode())
        .build()
        : null)
      .createdAt(task.getCreatedAt())
      .updatedAt(task.getUpdatedAt())
      .build();
  }
}
