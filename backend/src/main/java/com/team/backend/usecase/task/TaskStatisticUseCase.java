package com.team.backend.usecase.task;

import com.team.backend.dto.response.TaskStatsResponse;
import com.team.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskStatisticUseCase {
  private final TaskRepository taskRepository;

  public TaskStatsResponse execute(UUID currentUserId) {
    log.info("Statictis taks with uid: {}", currentUserId);
    return TaskStatsResponse.from(taskRepository.getStats(currentUserId, Instant.now()));
  }
}
