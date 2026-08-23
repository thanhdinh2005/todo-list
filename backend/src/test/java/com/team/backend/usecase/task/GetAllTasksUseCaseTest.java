package com.team.backend.usecase.task;

import com.team.backend.dto.request.task.TaskFilterParam;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.dto.response.TaskResponse;
import com.team.backend.entity.Task;
import com.team.backend.entity.User;
import com.team.backend.repository.TaskRepository;
import com.team.backend.utils.TaskTestFactory;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class GetAllTasksUseCaseTest {

  @Mock TaskRepository taskRepository;
  @InjectMocks GetAllTasksUseCase getAllTasksUseCase;

  private User owner;
  private Task task;

  @BeforeEach
  void setUp() {
    owner = UserTestFactory.createDefault();
    task = TaskTestFactory.createDefault(owner);
  }

  @Test
  void execute_returnsTasks_whenTasksExist() {
    TaskFilterParam filter = new TaskFilterParam();
    filter.setPage(0);
    filter.setSize(10);

    Page<Task> page = new PageImpl<>(
      List.of(task),
      filter.toPageable(),
      1
    );

    given(taskRepository.findAll(
      any(Specification.class),
      eq(filter.toPageable())
    )).willReturn(page);

    PageResponse<TaskResponse> response =
      getAllTasksUseCase.execute(
        filter,
        owner.getId()
      );

    assertThat(response.getContent()).hasSize(1);
    assertThat(response.getContent().get(0).getTitle()).isEqualTo(task.getTitle());
    assertThat(response.getPage()).isEqualTo(0);
    assertThat(response.getSize()).isEqualTo(10);
    assertThat(response.getTotalElements()).isEqualTo(1);
    assertThat(response.getTotalPages()).isEqualTo(1);
  }

  @Test
  void execute_returnsEmptyPage_whenNoTasksExist() {
    TaskFilterParam filter = new TaskFilterParam();
    filter.setPage(0);
    filter.setSize(10);

    Page<Task> page = new PageImpl<>(
      List.of(),
      filter.toPageable(),
      0
    );

    given(taskRepository.findAll(
      any(Specification.class),
      eq(filter.toPageable())
    )).willReturn(page);

    PageResponse<TaskResponse> response =
      getAllTasksUseCase.execute(
        filter,
        owner.getId()
      );

    assertThat(response.getContent()).isEmpty();
    assertThat(response.getTotalElements()).isZero();
    assertThat(response.getTotalPages()).isZero();
  }

  @Test
  void execute_returnsCorrectPaginationMetadata() {
    TaskFilterParam filter = new TaskFilterParam();
    filter.setPage(1);
    filter.setSize(2);

    Page<Task> page = new PageImpl<>(
      List.of(task),
      filter.toPageable(),
      5
    );

    given(taskRepository.findAll(
      any(Specification.class),
      eq(filter.toPageable())
    )).willReturn(page);

    PageResponse<TaskResponse> response =
      getAllTasksUseCase.execute(
        filter,
        owner.getId()
      );

    assertThat(response.getPage()).isEqualTo(1);
    assertThat(response.getSize()).isEqualTo(2);
    assertThat(response.getTotalElements()).isEqualTo(5);
    assertThat(response.getTotalPages()).isEqualTo(3);
  }

  @Test
  void execute_passesFilterPageableToRepository() {
    TaskFilterParam filter = new TaskFilterParam();
    filter.setPage(2);
    filter.setSize(5);
    filter.setSortBy("dueDate");
    filter.setDirection("ASC");

    Page<Task> page = new PageImpl<>(
      List.of(task),
      filter.toPageable(),
      11
    );

    given(taskRepository.findAll(
      any(Specification.class),
      any(Pageable.class)
    )).willReturn(page);

    getAllTasksUseCase.execute(
      filter,
      owner.getId()
    );

    then(taskRepository)
      .should()
      .findAll(
        any(Specification.class),
        eq(filter.toPageable())
      );
  }

  @Test
  void execute_buildsSpecificationWithFilterValues() {
    TaskFilterParam filter = new TaskFilterParam();

    filter.setCompleted(false);
    filter.setCategoryId(UUID.randomUUID());
    filter.setDueAfter(Instant.now().minusSeconds(3600));
    filter.setDueBefore(Instant.now());
    filter.setKeyword("grocery");

    Page<Task> page = new PageImpl<>(
      List.of(task),
      filter.toPageable(),
      1
    );

    given(taskRepository.findAll(
      any(Specification.class),
      any(Pageable.class)
    )).willReturn(page);

    getAllTasksUseCase.execute(
      filter,
      owner.getId()
    );

    then(taskRepository)
      .should()
      .findAll(
        any(Specification.class),
        eq(filter.toPageable())
      );
  }
}
