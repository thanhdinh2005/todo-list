package com.team.backend.usecase.category;

import com.team.backend.entity.Category;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeleteCategoryUseCase {
  private final CategoryRepository categoryRepository;

  public void execute(UUID currentUserId, UUID categoryId) {
    Category category = categoryRepository.findById(categoryId)
      .orElseThrow(() -> {
        log.warn("Category not found with id: {}", categoryId);
        return new AppException(
          ErrorCode.NOT_FOUND, "Category not found"
        );
      });

    if (!category.isOwnedBy(currentUserId)) {
      log.warn("Permission denied with uid: {} and categoryId: {}", currentUserId, categoryId);
      throw new AppException(
        ErrorCode.FORBIDDEN, "You are not permit to access this resource"
      );
    }

    categoryRepository.delete(category);
    log.info("Deleted category successfully with id: {}", categoryId);
  }
}
