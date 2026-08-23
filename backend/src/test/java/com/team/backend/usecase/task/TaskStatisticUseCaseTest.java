package com.team.backend.usecase.task;

import com.team.backend.dto.response.TaskStatsResponse;
import com.team.backend.projection.TaskStatsProjection;
import com.team.backend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TaskStatisticUseCaseTest {

  @Mock TaskRepository taskRepository;
  @Mock TaskStatsProjection projection;
  @InjectMocks TaskStatisticUseCase taskStatisticUseCase;

  private UUID currentUserId;

  @BeforeEach
  void setUp() {
    currentUserId = UUID.randomUUID();
  }

  @Test
  void execute_returnsStatistics_whenProjectionExists() {
    given(projection.getTotal()).willReturn(10L);

    given(projection.getCompleted()).willReturn(4L);
    given(projection.getPending()).willReturn(3L);
    given(projection.getOverdue()).willReturn(3L);
    given(taskRepository.getStats(
      eq(currentUserId),
      any(Instant.class)
    )).willReturn(projection);

    TaskStatsResponse response = taskStatisticUseCase.execute(currentUserId);

    assertThat(response).isNotNull();
    assertThat(response.getTotal()).isEqualTo(10L);
    assertThat(response.getCompleted()).isEqualTo(4L);
    assertThat(response.getPending()).isEqualTo(3L);
    assertThat(response.getOverdue()).isEqualTo(3L);
  }

  @Test
  void execute_returnsZeroStatistics_whenProjectionContainsZeros() {
    given(projection.getTotal()).willReturn(0L);
    given(projection.getCompleted()).willReturn(0L);
    given(projection.getPending()).willReturn(0L);
    given(projection.getOverdue()).willReturn(0L);

    given(taskRepository.getStats(
      eq(currentUserId),
      any(Instant.class)
    )).willReturn(projection);

    TaskStatsResponse response = taskStatisticUseCase.execute(currentUserId);

    assertThat(response.getTotal()).isZero();
    assertThat(response.getCompleted()).isZero();
    assertThat(response.getPending()).isZero();
    assertThat(response.getOverdue()).isZero();
  }

  @Test
  void execute_callsRepositoryWithCurrentUserId() {
    given(taskRepository.getStats(
      eq(currentUserId),
      any(Instant.class)
    )).willReturn(projection);

    taskStatisticUseCase.execute(currentUserId);

    then(taskRepository)
      .should()
      .getStats(
        eq(currentUserId),
        any(Instant.class)
      );
  }
}
