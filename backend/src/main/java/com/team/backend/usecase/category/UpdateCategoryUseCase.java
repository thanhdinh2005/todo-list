package com.team.backend.usecase.category;

import com.team.backend.dto.request.category.UpdateCategoryRequest;
import com.team.backend.dto.response.CategoryResponse;
import com.team.backend.entity.Category;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.CategoryRepository;
import com.team.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateCategoryUseCase {
  private final CategoryRepository categoryRepository;

  public CategoryResponse execute(UpdateCategoryRequest request, UUID categoryId, UUID currentUserId) {
    Category category = categoryRepository.findById(categoryId)
      .orElseThrow(() -> {
        log.warn("Category not found with id: {}", categoryId);
        return new AppException(
          ErrorCode.NOT_FOUND,
          "Category not found"
        );
      });

    if (!category.isOwnedBy(currentUserId)) {
      log.warn("Permission denied with uid: {} and categoryId: {}", currentUserId, categoryId);
      throw new AppException(
        ErrorCode.FORBIDDEN,
        "You are not permit to access this resource"
      );
    }

    if (request.getName() != null && !request.getName().isBlank())
      category.rename(request.getName());
    if (request.getColorCode() != null && !request.getColorCode().isBlank())
      category.changeColor(request.getColorCode());

    log.info("Update category successfully with id: {}", category.getId());
    return CategoryResponse.builder()
      .id(category.getId())
      .colorCode(category.getColorCode())
      .name(category.getName())
      .build();
  }
}
