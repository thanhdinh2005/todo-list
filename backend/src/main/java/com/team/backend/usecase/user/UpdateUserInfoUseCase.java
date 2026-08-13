package com.team.backend.usecase.user;

import com.team.backend.dto.request.user.UpdateUserRequest;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.entity.Role;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateUserInfoUseCase {
  private final UserRepository userRepository;

  public UserResponse execute(UpdateUserRequest request, UUID userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> {
        log.warn("User not found with id: {}", userId);
        return new AppException(ErrorCode.NOT_FOUND, "User not found with id: " + userId);
      });

    user.updateFullName(request.getFullName());
    log.info("Update full name successfully with uid: {}", userId);

    return UserResponse.builder()
      .id(user.getId())
      .enabled(user.isEnabled())
      .createdAt(user.getCreatedAt())
      .fullName(request.getFullName())
      .email(user.getEmail())
      .roles(
        user.getRoles()
          .stream()
          .map(Role::getName)
          .toList()
      )
      .build();
  }
}
