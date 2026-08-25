package com.team.backend.usecase.category;

import com.team.backend.entity.Category;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.CategoryRepository;
import com.team.backend.utils.CategoryTestFactory;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class DeleteCategoryUseCaseTest {

  @Mock CategoryRepository categoryRepository;
  @InjectMocks DeleteCategoryUseCase deleteCategoryUseCase;

  private User owner;
  private Category category;

  @BeforeEach
  void setUp() {
    owner = UserTestFactory.createDefault();
    category = CategoryTestFactory.createDefault(owner);
  }

  @Test
  void execute_deletesCategory_whenOwnedByCurrentUser() {
    given(categoryRepository
      .findById(category.getId()))
      .willReturn(Optional.of(category));

    deleteCategoryUseCase.execute(owner.getId(), category.getId());

    then(categoryRepository).should().delete(category);
  }

  @Test
  void execute_throwsNotFound_whenCategoryDoesNotExist() {
    UUID missingId = UUID.randomUUID();
    given(categoryRepository
      .findById(missingId))
      .willReturn(Optional.empty());

    assertThatThrownBy(() -> deleteCategoryUseCase
      .execute(owner.getId(), missingId))
      .isInstanceOf(AppException.class)
      .satisfies(ex -> assertThat(((AppException) ex)
        .getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND));

    then(categoryRepository).should(never()).delete(any());
  }

  @Test
  void execute_throwsForbidden_whenCategoryOwnedByAnotherUser() {
    User otherUser = UserTestFactory.createDefault();
    given(categoryRepository
      .findById(category.getId()))
      .willReturn(Optional.of(category));

    assertThatThrownBy(() -> deleteCategoryUseCase
      .execute(otherUser.getId(), category.getId()))
      .isInstanceOf(AppException.class)
      .satisfies(ex -> assertThat(((AppException) ex)
        .getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN));

    then(categoryRepository).should(never()).delete(any());
  }
}
