package com.team.backend.usecase.category;

import com.team.backend.dto.request.category.CreateCategoryRequest;
import com.team.backend.dto.response.CategoryResponse;
import com.team.backend.entity.Category;
import com.team.backend.entity.User;
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
public class CreateCategoryUseCase {
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  public CategoryResponse execute(CreateCategoryRequest request, UUID currentUserId) {
    User user = userRepository.findById(currentUserId)
      .orElseThrow(() -> {
        log.warn("User not found with id: {}", currentUserId);
        return new AppException(
          ErrorCode.NOT_FOUND,
          "User not found with id: " + currentUserId
        );
      });

    if (categoryRepository.existsByNameAndOwnerId(request.getName(), currentUserId)) {
      throw new AppException(
        ErrorCode.CONFLICT,
        "Category name already exists"
      );
    }

    Category category = Category.create(request.getName(), request.getColorCode(), user);
    category = categoryRepository.save(category);

    log.info("Created category successfully with id: {}", category.getId());
    return CategoryResponse.builder()
      .id(category.getId())
      .name(category.getName())
      .colorCode(category.getColorCode())
      .build();
  }
}
