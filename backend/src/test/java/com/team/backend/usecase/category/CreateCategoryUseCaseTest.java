package com.team.backend.usecase.category;

import com.team.backend.dto.request.category.CreateCategoryRequest;
import com.team.backend.dto.response.CategoryResponse;
import com.team.backend.entity.Category;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.CategoryRepository;
import com.team.backend.repository.UserRepository;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

  @Mock UserRepository userRepository;
  @Mock CategoryRepository categoryRepository;
  @InjectMocks CreateCategoryUseCase createCategoryUseCase;

  private User owner;
  private CreateCategoryRequest request;

  @BeforeEach
  void setUp() {
    owner = UserTestFactory.createDefault();
    request = new CreateCategoryRequest("Work", "#FF5733");
  }

  @Test
  void execute_createsCategory_whenValidRequest() {
    given(userRepository.findById(
      owner.getId())).willReturn(Optional.of(owner)
    );
    given(categoryRepository
      .existsByNameAndOwnerId(
        "Work", owner.getId())
    ).willReturn(false);
    given(categoryRepository
      .save(any(Category.class)))
      .willAnswer(inv -> inv.getArgument(0));

    CategoryResponse response = createCategoryUseCase.execute(request, owner.getId());

    assertThat(response.getName()).isEqualTo("Work");
    assertThat(response.getColorCode()).isEqualTo("#FF5733");

    ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
    then(categoryRepository).should().save(captor.capture());
    assertThat(captor.getValue().isOwnedBy(owner.getId())).isTrue();
  }

  @Test
  void execute_throwsNotFound_whenUserDoesNotExist() {
    UUID missingUserId = UUID.randomUUID();
    given(userRepository.findById(missingUserId)).willReturn(Optional.empty());

    assertThatThrownBy(
      () -> createCategoryUseCase
        .execute(request, missingUserId))
      .isInstanceOf(AppException.class)
      .satisfies(
        ex -> assertThat(((AppException) ex)
          .getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND));

    then(categoryRepository).should(never()).save(any());
  }

  @Test
  void execute_throwsConflict_whenCategoryNameAlreadyExists() {
    given(userRepository.findById(
      owner.getId())).willReturn(Optional.of(owner)
    );
    given(categoryRepository
      .existsByNameAndOwnerId("Work", owner.getId()))
      .willReturn(true);

    assertThatThrownBy(() -> createCategoryUseCase
      .execute(request, owner.getId()))
      .isInstanceOf(AppException.class)
      .satisfies(ex -> assertThat(((AppException) ex)
        .getErrorCode()).isEqualTo(ErrorCode.CONFLICT));

    then(categoryRepository).should(never()).save(any());
  }

  @Test
  void execute_throwsBadRequest_whenColorCodeInvalid() {
    var badRequest = new CreateCategoryRequest("Work", "not-a-color");
    given(userRepository
      .findById(owner.getId()))
      .willReturn(Optional.of(owner));
    given(categoryRepository
      .existsByNameAndOwnerId("Work", owner.getId())).willReturn(false);

    assertThatThrownBy(() -> createCategoryUseCase
      .execute(badRequest, owner.getId()))
      .isInstanceOf(AppException.class);

    then(categoryRepository).should(never()).save(any());
  }
}
