package com.team.backend.projection;

public interface TaskStatsProjection {
  Long getTotal();
  Long getCompleted();
  Long getPending();
  Long getOverdue();
}
