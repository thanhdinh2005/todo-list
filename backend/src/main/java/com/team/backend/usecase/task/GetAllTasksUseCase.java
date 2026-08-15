package com.team.backend.usecase.task;

import com.team.backend.dto.request.task.TaskFilterParam;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.dto.response.TaskResponse;
import com.team.backend.entity.Task;
import com.team.backend.repository.TaskRepository;
import com.team.backend.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetAllTasksUseCase {
  private final TaskRepository taskRepository;

  public PageResponse<TaskResponse> execute(TaskFilterParam filter, UUID currentUserId) {
    Specification<Task> spec = TaskSpecification.buildFilter(
      currentUserId,
      filter.getCompleted(),
      filter.getCategoryId(),
      filter.getDueAfter(),
      filter.getDueBefore(),
      filter.getKeyword()
    );

    Page<Task> page = taskRepository.findAll(spec, filter.toPageable());

    List<TaskResponse> items = page.getContent()
      .stream()
      .map(TaskResponse::from)
      .toList();

    log.info("Get tasks for user {} with {} elements", currentUserId, page.getTotalElements());

    return PageResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
  }
}
