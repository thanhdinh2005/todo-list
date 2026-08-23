package com.team.backend.usecase.task;

import com.team.backend.dto.request.task.CreateTaskRequest;
import com.team.backend.dto.response.TaskResponse;
import com.team.backend.entity.Category;
import com.team.backend.entity.Task;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.CategoryRepository;
import com.team.backend.repository.TaskRepository;
import com.team.backend.repository.UserRepository;
import com.team.backend.utils.CategoryTestFactory;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CreateTaskUseCaseTest {

  @Mock TaskRepository taskRepository;
  @Mock UserRepository userRepository;
  @Mock CategoryRepository categoryRepository;

  @InjectMocks CreateTaskUseCase createTaskUseCase;

  private User owner;
  private Category category;
  private CreateTaskRequest request;

  @BeforeEach
  void setUp() {
    owner = UserTestFactory.createDefault();

    category = CategoryTestFactory.createDefault(owner);

    request = new CreateTaskRequest(
      "Buy groceries",
      "Milk, eggs, bread",
      Instant.now().plusSeconds(86400),
      category.getId()
    );
  }

  @Test
  void execute_createsTask_whenValidRequestWithoutCategory() {
    request = new CreateTaskRequest(
      "Buy groceries",
      "Milk, eggs, bread",
      Instant.now().plusSeconds(86400),
      null
    );

    given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
    given(taskRepository.save(any(Task.class))).willAnswer(inv -> inv.getArgument(0));

    TaskResponse response = createTaskUseCase.execute(owner.getId(), request);

    assertThat(response.getTitle()).isEqualTo("Buy groceries");
    assertThat(response.getDescription()).isEqualTo("Milk, eggs, bread");

    ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

    then(taskRepository).should().save(captor.capture());

    Task savedTask = captor.getValue();

    assertThat(savedTask.getTitle()).isEqualTo("Buy groceries");
    assertThat(savedTask.getDescription()).isEqualTo("Milk, eggs, bread");
    assertThat(savedTask.getOwner()).isEqualTo(owner);
    assertThat(savedTask.getCategory()).isNull();
  }

  @Test
  void execute_createsTask_whenValidRequestWithOwnedCategory() {
    given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
    given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));
    given(taskRepository.save(any(Task.class))).willAnswer(inv -> inv.getArgument(0));

    TaskResponse response = createTaskUseCase.execute(owner.getId(), request);

    assertThat(response.getTitle()).isEqualTo("Buy groceries");

    ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

    then(taskRepository).should().save(captor.capture());

    Task savedTask = captor.getValue();

    assertThat(savedTask.getOwner()).isEqualTo(owner);
    assertThat(savedTask.getCategory()).isEqualTo(category);
    assertThat(savedTask.getTitle()).isEqualTo(request.getTitle());
    assertThat(savedTask.getDescription()).isEqualTo(request.getDescription());
    assertThat(savedTask.getDueDate()).isEqualTo(request.getDueDate());
  }

  @Test
  void execute_throwsNotFound_whenUserDoesNotExist() {
    UUID missingUserId = UUID.randomUUID();

    given(userRepository.findById(missingUserId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> createTaskUseCase.execute(
        missingUserId,
        request
      ))
      .isInstanceOf(AppException.class)
      .satisfies(ex ->
        assertThat(((AppException) ex).getErrorCode())
          .isEqualTo(ErrorCode.NOT_FOUND)
      );

    then(categoryRepository).should(never()).findById(any());
    then(taskRepository).should(never()).save(any());
  }

  @Test
  void execute_throwsNotFound_whenCategoryDoesNotExist() {
    given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
    given(categoryRepository.findById(category.getId())).willReturn(Optional.empty());

    assertThatThrownBy(() -> createTaskUseCase.execute(
        owner.getId(),
        request))
      .isInstanceOf(AppException.class)
      .satisfies(ex ->
        assertThat(((AppException) ex).getErrorCode())
          .isEqualTo(ErrorCode.NOT_FOUND)
      );

    then(taskRepository).should(never()).save(any());
  }

  @Test
  void execute_throwsForbidden_whenCategoryDoesNotBelongToUser() {
    User anotherUser = UserTestFactory.createDefault();

    Category anotherUsersCategory = CategoryTestFactory.createDefault(anotherUser);

    request = new CreateTaskRequest(
      "Buy groceries",
      "Milk, eggs, bread",
      Instant.now().plusSeconds(86400),
      anotherUsersCategory.getId()
    );

    given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
    given(categoryRepository.findById(anotherUsersCategory.getId())).willReturn(Optional.of(anotherUsersCategory));

    assertThatThrownBy(() -> createTaskUseCase.execute(
        owner.getId(),
        request
      ))
      .isInstanceOf(AppException.class)
      .satisfies(ex ->
        assertThat(((AppException) ex).getErrorCode())
          .isEqualTo(ErrorCode.FORBIDDEN)
      );

    then(taskRepository).should(never()).save(any());
  }

  @Test
  void execute_doesNotLoadCategory_whenCategoryIdIsNull() {
    request = new CreateTaskRequest(
      "Buy groceries",
      "Milk, eggs, bread",
      Instant.now().plusSeconds(86400),
      null
    );

    given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
    given(taskRepository.save(any(Task.class))).willAnswer(inv -> inv.getArgument(0));

    createTaskUseCase.execute(
      owner.getId(),
      request
    );

    then(categoryRepository).should(never()).findById(any());
    then(taskRepository).should().save(any(Task.class));
  }

  @Test
  void execute_savesTaskOnlyOnce_whenValidRequest() {
    request = new CreateTaskRequest(
      "Buy groceries",
      "Milk, eggs, bread",
      Instant.now().plusSeconds(86400),
      null
    );

    given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
    given(taskRepository.save(any(Task.class))).willAnswer(inv -> inv.getArgument(0));

    createTaskUseCase.execute(
      owner.getId(),
      request
    );

    then(taskRepository).should(times(1))
      .save(any(Task.class));
  }
}
