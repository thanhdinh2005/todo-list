package com.team.backend.dto.response;

import java.time.Instant;
import java.util.UUID;

public class TaskResponse {
  private UUID id;
  private String title;
  private String description;
  private boolean completed;
  private Instant dueDate;
  private CategoryResponse category;
  private Instant createdAt;
  private Instant updatedAt;
}
