package com.team.backend.dto.request.task;

import java.time.LocalDateTime;

public class UpdateTaskRequest {
  private String title;
  private String description;
  private LocalDateTime dueDate;
  private Integer categoryId;
}
