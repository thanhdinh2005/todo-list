package com.team.backend.dto.request.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateTaskRequest {
  private String title;
  private String description;
  private Instant dueDate;
  private UUID categoryId;
}
