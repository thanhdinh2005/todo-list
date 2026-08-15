package com.team.backend.dto.response;

import com.team.backend.projection.TaskStatsProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskStatsResponse {
  private Long total;
  private Long completed;
  private Long pending;
  private Long overdue;

  public static TaskStatsResponse from(TaskStatsProjection projection) {
    return TaskStatsResponse.builder()
      .total(projection.getTotal())
      .completed(projection.getCompleted())
      .pending(projection.getPending())
      .overdue(projection.getOverdue())
      .build();
  }
}
