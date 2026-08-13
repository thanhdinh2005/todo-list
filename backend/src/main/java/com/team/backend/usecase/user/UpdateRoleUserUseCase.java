package com.team.backend.usecase.user;

import com.team.backend.dto.request.user.UpdateUserRolesRequest;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.entity.Role;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RoleRepository;
import com.team.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateRoleUserUseCase {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public UserResponse execute(
    UpdateUserRolesRequest request,
    UUID userId,
    UUID currentUserId
  ) {
    if (userId.equals(currentUserId)) {
      throw new AppException(
        ErrorCode.BAD_REQUEST,
        "You cannot change your own roles"
      );
    }

    User user = userRepository.findById(userId)
      .orElseThrow(() -> {
        log.warn("User not found with id: {}", userId);
        return new AppException(
          ErrorCode.NOT_FOUND,
          "User not found with id: " + userId
        );
      });

    Set<UUID> uniqueRoleIds = new HashSet<>(request.getRoleIds());

    List<Role> roles = roleRepository.findAllById(uniqueRoleIds);

    if (roles.size() != uniqueRoleIds.size()) {
      throw new AppException(
        ErrorCode.BAD_REQUEST,
        "Role not found"
      );
    }

    user.changeRoles(roles);

    log.info("Changed roles successfully for user: {}", userId);

    return UserResponse.builder()
      .id(user.getId())
      .fullName(user.getFullName())
      .email(user.getEmail())
      .roles(user.getRoles()
        .stream()
        .map(Role::getName)
        .toList())
      .createdAt(user.getCreatedAt())
      .enabled(user.isEnabled())
      .build();
  }
}
