package com.team.backend.dto.request.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateTaskRequest {
  @NotBlank(message = "Title is required")
  @Size(max = 100, message = "Title must not exceed 100 characters")
  private String title;
  private String description;
  private Instant dueDate;
  private UUID categoryId;
}
