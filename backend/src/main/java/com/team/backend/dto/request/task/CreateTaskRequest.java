package com.team.backend.dto.request.task;

import java.time.LocalDateTime;

public class CreateTaskRequest {
  private String title;
  private String description;
  private LocalDateTime dueDate;
  private Integer categoryId;
}
