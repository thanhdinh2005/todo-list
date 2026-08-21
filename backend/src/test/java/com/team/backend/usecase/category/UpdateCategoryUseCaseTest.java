package com.team.backend.usecase.category;

import com.team.backend.dto.request.category.UpdateCategoryRequest;
import com.team.backend.dto.response.CategoryResponse;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

  @Mock CategoryRepository categoryRepository;
  @InjectMocks UpdateCategoryUseCase updateCategoryUseCase;

  private User owner;
  private Category category;

  @BeforeEach
  void setUp() {
    owner = UserTestFactory.createDefault();
    category = CategoryTestFactory
      .create(UUID.randomUUID(), "Work", "#FF5733", owner);
  }

  @Test
  void execute_updatesBothFields_whenBothProvided() {
    var request = new UpdateCategoryRequest("Personal", "#00FF00");
    given(categoryRepository
      .findById(category.getId()))
      .willReturn(Optional.of(category));

    CategoryResponse response = updateCategoryUseCase
      .execute(request, category.getId(), owner.getId());

    assertThat(response.getName()).isEqualTo("Personal");
    assertThat(response.getColorCode()).isEqualTo("#00FF00");
  }

  @Test
  void execute_updatesNameOnly_whenColorCodeBlank() {
    var request = new UpdateCategoryRequest("Personal", "");
    given(categoryRepository
      .findById(category.getId()))
      .willReturn(Optional.of(category));

    CategoryResponse response = updateCategoryUseCase
      .execute(request, category.getId(), owner.getId());

    assertThat(response.getName()).isEqualTo("Personal");
    assertThat(response.getColorCode()).isEqualTo("#FF5733");
  }

  @Test
  void execute_updatesColorOnly_whenNameNull() {
    var request = new UpdateCategoryRequest(null, "#00FF00");
    given(categoryRepository
      .findById(category.getId()))
      .willReturn(Optional.of(category));

    CategoryResponse response = updateCategoryUseCase
      .execute(request, category.getId(), owner.getId());

    assertThat(response.getName()).isEqualTo("Work");
    assertThat(response.getColorCode()).isEqualTo("#00FF00");
  }

  @Test
  void execute_leavesFieldsUnchanged_whenBothNullOrBlank() {
    var request = new UpdateCategoryRequest(null, "  ");
    given(categoryRepository
      .findById(category.getId()))
      .willReturn(Optional.of(category));

    CategoryResponse response = updateCategoryUseCase
      .execute(request, category.getId(), owner.getId());

    assertThat(response.getName()).isEqualTo("Work");
    assertThat(response.getColorCode()).isEqualTo("#FF5733");
  }

  @Test
  void execute_throwsBadRequest_whenNewColorCodeInvalid() {
    var request = new UpdateCategoryRequest(null, "not-a-color");
    given(categoryRepository.findById(category.getId()))
      .willReturn(Optional.of(category));

    assertThatThrownBy(() -> updateCategoryUseCase
      .execute(request, category.getId(), owner.getId()))
      .isInstanceOf(AppException.class)
      .satisfies(ex -> assertThat(((AppException) ex)
        .getErrorCode()).isEqualTo(ErrorCode.BAD_REQUEST));
  }

  @Test
  void execute_throwsNotFound_whenCategoryDoesNotExist() {
    UUID missingId = UUID.randomUUID();
    var request = new UpdateCategoryRequest("Personal", "#00FF00");
    given(categoryRepository.findById(missingId))
      .willReturn(Optional.empty());

    assertThatThrownBy(() -> updateCategoryUseCase
      .execute(request, missingId, owner.getId()))
      .isInstanceOf(AppException.class)
      .satisfies(ex -> assertThat(((AppException) ex)
        .getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND));
  }

  @Test
  void execute_throwsForbidden_whenCategoryOwnedByAnotherUser() {
    User otherUser = UserTestFactory.createDefault();
    var request = new UpdateCategoryRequest("Personal", "#00FF00");
    given(categoryRepository.findById(category.getId()))
      .willReturn(Optional.of(category));

    assertThatThrownBy(() -> updateCategoryUseCase
      .execute(request, category.getId(), otherUser.getId()))
      .isInstanceOf(AppException.class)
      .satisfies(ex -> assertThat(((AppException) ex)
        .getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN));
  }
}
