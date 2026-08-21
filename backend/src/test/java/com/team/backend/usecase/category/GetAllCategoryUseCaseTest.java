package com.team.backend.usecase.category;

import com.team.backend.dto.response.CategoryResponse;
import com.team.backend.entity.Category;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.CategoryRepository;
import com.team.backend.repository.UserRepository;
import com.team.backend.utils.CategoryTestFactory;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class GetAllCategoryUseCaseTest {

  @Mock
  UserRepository userRepository;
  @Mock CategoryRepository categoryRepository;
  @InjectMocks GetAllCategoryUseCase getAllCategoryUseCase;

  private User owner;

  @BeforeEach
  void setUp() {
    owner = UserTestFactory.createDefault();
  }

  @Test
  void execute_returnsMappedCategories_whenUserExists() {
    Category cat1 = CategoryTestFactory
      .create(UUID.randomUUID(), "Work", "#FF5733", owner);
    Category cat2 = CategoryTestFactory
      .create(UUID.randomUUID(), "Personal", "#00FF00", owner);

    given(userRepository.existsById(owner.getId())).willReturn(true);
    given(categoryRepository
      .findAllByOwnerId(owner.getId()))
      .willReturn(List.of(cat1, cat2));

    List<CategoryResponse> result = getAllCategoryUseCase.execute(owner.getId());

    assertThat(result).hasSize(2);
    assertThat(result)
      .extracting(CategoryResponse::getName, CategoryResponse::getColorCode)
      .containsExactlyInAnyOrder(
        tuple("Work", "#FF5733"),
        tuple("Personal", "#00FF00")
      );
  }

  @Test
  void execute_returnsEmptyList_whenUserHasNoCategories() {
    given(userRepository
      .existsById(owner.getId()))
      .willReturn(true);
    given(categoryRepository
      .findAllByOwnerId(owner.getId()))
      .willReturn(List.of());

    List<CategoryResponse> result = getAllCategoryUseCase.execute(owner.getId());

    assertThat(result).isEmpty();
  }

  @Test
  void execute_throwsNotFound_whenUserDoesNotExist() {
    UUID missingUserId = UUID.randomUUID();
    given(userRepository.existsById(missingUserId)).willReturn(false);

    assertThatThrownBy(() -> getAllCategoryUseCase
      .execute(missingUserId))
      .isInstanceOf(AppException.class)
      .satisfies(ex -> assertThat(((AppException) ex)
        .getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND));

    then(categoryRepository).should(never()).findAllByOwnerId(any());
  }
}
