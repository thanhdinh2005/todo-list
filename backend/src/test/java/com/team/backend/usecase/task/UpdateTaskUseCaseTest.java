package com.team.backend.usecase.task;

import com.team.backend.dto.request.task.UpdateTaskRequest;
import com.team.backend.dto.response.TaskResponse;
import com.team.backend.entity.Category;
import com.team.backend.entity.Task;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.CategoryRepository;
import com.team.backend.repository.TaskRepository;
import com.team.backend.utils.CategoryTestFactory;
import com.team.backend.utils.TaskTestFactory;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UpdateTaskUseCaseTest {

  @Mock TaskRepository taskRepository;
  @Mock CategoryRepository categoryRepository;
  @InjectMocks UpdateTaskUseCase updateTaskUseCase;

  private User owner;
  private Task task;
  private Category category;

  @BeforeEach
  void setUp() {
    owner = UserTestFactory.createDefault();
    task = TaskTestFactory.createDefault(owner);
    category = CategoryTestFactory.createDefault(owner);
  }

  @Test
  void execute_updatesTitle_whenValidTitleProvided() {
    given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

    UpdateTaskRequest request = new UpdateTaskRequest();
    request.setTitle("Updated title");

    TaskResponse response =
      updateTaskUseCase.execute(
        owner.getId(),
        task.getId(),
        request
      );

    assertThat(task.getTitle()).isEqualTo("Updated title");
    assertThat(response.getTitle()).isEqualTo("Updated title");

    then(categoryRepository).shouldHaveNoInteractions();
  }

  @Test
  void execute_updatesDescription_whenDescriptionProvided() {
    given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

    UpdateTaskRequest request = new UpdateTaskRequest();
    request.setDescription("Updated description");

    TaskResponse response =
      updateTaskUseCase.execute(
        owner.getId(),
        task.getId(),
        request
      );

    assertThat(task.getDescription()).isEqualTo("Updated description");
    assertThat(response.getDescription()).isEqualTo("Updated description");

    then(categoryRepository).shouldHaveNoInteractions();
  }

  @Test
  void execute_updatesDueDate_whenDueDateProvided() {
    given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

    Instant newDueDate = Instant.now().plusSeconds(86400 * 3);

    UpdateTaskRequest request = new UpdateTaskRequest();
    request.setDueDate(newDueDate);

    TaskResponse response =
      updateTaskUseCase.execute(
        owner.getId(),
        task.getId(),
        request
      );

    assertThat(task.getDueDate()).isEqualTo(newDueDate);
    assertThat(response.getDueDate()).isEqualTo(newDueDate);

    then(categoryRepository).shouldHaveNoInteractions();
  }

  @Test
  void execute_updatesCategory_whenCategoryBelongsToCurrentUser() {
    given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

    given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

    UpdateTaskRequest request = new UpdateTaskRequest();
    request.setCategoryId(category.getId());

    TaskResponse response =
      updateTaskUseCase.execute(
        owner.getId(),
        task.getId(),
        request
      );

    assertThat(task.getCategory()).isEqualTo(category);
    assertThat(response.getCategory()).isNotNull();

    then(categoryRepository)
      .should()
      .findById(category.getId());
  }

  @Test
  void execute_updatesMultipleFields_whenValidRequest() {
    given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

    given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

    Instant newDueDate = Instant.now().plusSeconds(86400 * 3);

    UpdateTaskRequest request = new UpdateTaskRequest();

    request.setTitle("Updated title");
    request.setDescription("Updated description");
    request.setDueDate(newDueDate);
    request.setCategoryId(category.getId());

    TaskResponse response =
      updateTaskUseCase.execute(
        owner.getId(),
        task.getId(),
        request
      );

    assertThat(task.getTitle()).isEqualTo("Updated title");
    assertThat(task.getDescription()).isEqualTo("Updated description");
    assertThat(task.getDueDate()).isEqualTo(newDueDate);
    assertThat(task.getCategory()).isEqualTo(category);
    assertThat(response.getTitle()).isEqualTo("Updated title");
    assertThat(response.getDescription()).isEqualTo("Updated description");
    assertThat(response.getDueDate()).isEqualTo(newDueDate);
  }

  @Test
  void execute_keepsExistingValues_whenFieldsAreNotProvided() {
    given(taskRepository.findById(task.getId()))
      .willReturn(Optional.of(task));

    String originalTitle = task.getTitle();
    String originalDescription = task.getDescription();
    Instant originalDueDate = task.getDueDate();

    UpdateTaskRequest request = new UpdateTaskRequest();

    updateTaskUseCase.execute(
      owner.getId(),
      task.getId(),
      request
    );

    assertThat(task.getTitle()).isEqualTo(originalTitle);
    assertThat(task.getDescription()).isEqualTo(originalDescription);
    assertThat(task.getDueDate()).isEqualTo(originalDueDate);
    assertThat(task.getCategory()).isNull();

    then(categoryRepository).shouldHaveNoInteractions();
  }

  @Test
  void execute_ignoresBlankTitle() {
    given(taskRepository.findById(task.getId()))
      .willReturn(Optional.of(task));

    String originalTitle = task.getTitle();

    UpdateTaskRequest request = new UpdateTaskRequest();
    request.setTitle("   ");

    updateTaskUseCase.execute(
      owner.getId(),
      task.getId(),
      request
    );

    assertThat(task.getTitle()).isEqualTo(originalTitle);
  }

  @Test
  void execute_throwsNotFound_whenTaskDoesNotExist() {
    UUID taskId = UUID.randomUUID();

    given(taskRepository.findById(taskId))
      .willReturn(Optional.empty());

    UpdateTaskRequest request = new UpdateTaskRequest();
    request.setTitle("Updated title");

    assertThatThrownBy(() ->
      updateTaskUseCase.execute(
        owner.getId(),
        taskId,
        request
      )
    )
      .isInstanceOf(AppException.class)
      .satisfies(ex ->
        assertThat(((AppException) ex).getErrorCode())
          .isEqualTo(ErrorCode.NOT_FOUND)
      );

    then(categoryRepository).shouldHaveNoInteractions();
  }

  @Test
  void execute_throwsForbidden_whenTaskBelongsToAnotherUser() {
    User anotherUser = UserTestFactory.createDefault();

    given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

    UpdateTaskRequest request = new UpdateTaskRequest();
    request.setTitle("Hacked title");

    assertThatThrownBy(() ->
      updateTaskUseCase.execute(
        anotherUser.getId(),
        task.getId(),
        request
      )
    )
      .isInstanceOf(AppException.class)
      .satisfies(ex ->
        assertThat(((AppException) ex).getErrorCode())
          .isEqualTo(ErrorCode.FORBIDDEN)
      );

    assertThat(task.getTitle()).isNotEqualTo("Hacked title");

    then(categoryRepository).shouldHaveNoInteractions();
  }

  @Test
  void execute_throwsNotFound_whenCategoryDoesNotExist() {
    UUID categoryId = UUID.randomUUID();

    given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

    given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

    UpdateTaskRequest request = new UpdateTaskRequest();
    request.setCategoryId(categoryId);

    assertThatThrownBy(() ->
      updateTaskUseCase.execute(
        owner.getId(),
        task.getId(),
        request
      )
    )
      .isInstanceOf(AppException.class)
      .satisfies(ex ->
        assertThat(((AppException) ex).getErrorCode())
          .isEqualTo(ErrorCode.NOT_FOUND)
      );

    assertThat(task.getCategory()).isNull();
  }

  @Test
  void execute_throwsForbidden_whenCategoryBelongsToAnotherUser() {
    User anotherUser = UserTestFactory.createDefault();

    Category anotherCategory = CategoryTestFactory.createDefault(anotherUser);

    given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));
    given(categoryRepository.findById(anotherCategory.getId())).willReturn(Optional.of(anotherCategory));

    UpdateTaskRequest request = new UpdateTaskRequest();
    request.setCategoryId(anotherCategory.getId());

    assertThatThrownBy(() ->
      updateTaskUseCase.execute(
        owner.getId(),
        task.getId(),
        request
      )
    )
      .isInstanceOf(AppException.class)
      .satisfies(ex ->
        assertThat(((AppException) ex).getErrorCode())
          .isEqualTo(ErrorCode.FORBIDDEN)
      );

    assertThat(task.getCategory()).isNull();
  }

  @Test
  void execute_doesNotLoadCategory_whenCategoryIdIsNull() {
    given(taskRepository.findById(task.getId()))
      .willReturn(Optional.of(task));

    UpdateTaskRequest request = new UpdateTaskRequest();
    request.setTitle("Updated title");

    updateTaskUseCase.execute(
      owner.getId(),
      task.getId(),
      request
    );

    then(categoryRepository).shouldHaveNoInteractions();
  }
}
