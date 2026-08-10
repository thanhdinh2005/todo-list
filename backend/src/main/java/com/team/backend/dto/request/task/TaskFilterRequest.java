package com.team.backend.dto.request.task;

import java.time.LocalDateTime;

public class TaskFilterRequest {
  private Boolean completed;
  private Integer categoryId;
  private LocalDateTime dueBefore;
  private LocalDateTime dueAfter;
  private String keyword;
  private int page;
  private int size;
  private String sort;
}
