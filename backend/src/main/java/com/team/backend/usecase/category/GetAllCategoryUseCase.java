package com.team.backend.usecase.category;

import com.team.backend.dto.response.CategoryResponse;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.CategoryRepository;
import com.team.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetAllCategoryUseCase {
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  public List<CategoryResponse> execute(UUID currentUserId) {
    if (!userRepository.existsById(currentUserId)) {
      log.warn("User not found with id: {}", currentUserId);
      throw new AppException(
        ErrorCode.NOT_FOUND,
        "User not found with id: " + currentUserId
      );
    }

    log.info("Get all categories successfuly");

    return categoryRepository.findAllByOwnerId(currentUserId)
      .stream()
      .map(c ->CategoryResponse.builder()
        .id(c.getId())
        .colorCode(c.getColorCode())
        .name(c.getName())
        .build())
      .toList();
  }
}
