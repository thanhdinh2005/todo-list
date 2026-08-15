package com.team.backend.dto.request.task;

import com.team.backend.dto.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskFilterParam extends BasePageRequest {
  private static final Set<String> ALLOWED_SORT = Set.of("createdAt", "updatedAt", "dueDate", "title");

  private Boolean completed;
  private UUID categoryId;
  private Instant dueBefore;
  private Instant dueAfter;
  private String keyword;

  @Override
  protected Set<String> getAllowedSortFields() {
    return ALLOWED_SORT;
  }
}
