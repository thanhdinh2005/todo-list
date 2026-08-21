package com.team.backend.usecase.auth;

import com.team.backend.dto.response.UserResponse;
import com.team.backend.entity.Role;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserProfileUseCase {
  private final UserRepository userRepository;

  public UserResponse getMe(UUID currentUserId) {
    User user = userRepository.findByIdWithRoles(currentUserId)
      .orElseThrow( () -> {
        log.warn("User not found");
        return new AppException(ErrorCode.NOT_FOUND, "User not found");
      });

    return UserResponse.builder()
      .id(user.getId())
      .email(user.getEmail())
      .enabled(user.isEnabled())
      .fullName(user.getFullName())
      .roles(user.getRoles().stream().map(Role::getName).toList())
      .createdAt(user.getCreatedAt())
      .build();
  }
}
