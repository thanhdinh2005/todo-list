package com.team.backend.usecase.task;

import com.team.backend.dto.response.TaskResponse;
import com.team.backend.entity.Task;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MarkAsCompleteTaskUseCaseTest {

  @Mock TaskRepository taskRepository;
  @InjectMocks MarkAsCompleteTaskUseCase markAsCompleteTaskUseCase;

  private User owner;
  private Task task;

  @BeforeEach
  void setUp() {
    owner = UserTestFactory.createDefault();
    task = TaskTestFactory.createDefault(owner);
  }

  @Test
  void execute_marksTaskAsCompleted_whenTaskBelongsToCurrentUser() {
    given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

    assertThat(task.isCompleted()).isFalse();

    TaskResponse response =
      markAsCompleteTaskUseCase.execute(
        owner.getId(),
        task.getId()
      );

    assertThat(task.isCompleted()).isTrue();
    assertThat(response).isNotNull();
    assertThat(response.isCompleted()).isTrue();

    then(taskRepository).should().findById(task.getId());
  }

  @Test
  void execute_throwsNotFound_whenTaskDoesNotExist() {
    UUID taskId = UUID.randomUUID();

    given(taskRepository.findById(taskId)).willReturn(Optional.empty());

    assertThatThrownBy(() ->
      markAsCompleteTaskUseCase.execute(
        owner.getId(),
        taskId
      )
    )
      .isInstanceOf(AppException.class)
      .satisfies(ex ->
        assertThat(((AppException) ex).getErrorCode())
          .isEqualTo(ErrorCode.NOT_FOUND)
      );

    then(taskRepository).should().findById(taskId);
  }

  @Test
  void execute_throwsForbidden_whenTaskBelongsToAnotherUser() {
    User anotherUser = UserTestFactory.createDefault();

    given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

    assertThatThrownBy(() ->
      markAsCompleteTaskUseCase.execute(
        anotherUser.getId(),
        task.getId()
      )
    )
      .isInstanceOf(AppException.class)
      .satisfies(ex ->
        assertThat(((AppException) ex).getErrorCode())
          .isEqualTo(ErrorCode.FORBIDDEN)
      );

    assertThat(task.isCompleted()).isFalse();
  }

  @Test
  void execute_canCompleteAlreadyCompletedTask() {
    task.complete();

    given(taskRepository.findById(task.getId()))
      .willReturn(Optional.of(task));

    TaskResponse response =
      markAsCompleteTaskUseCase.execute(
        owner.getId(),
        task.getId()
      );

    assertThat(task.isCompleted()).isTrue();
    assertThat(response.isCompleted()).isTrue();
  }
}
