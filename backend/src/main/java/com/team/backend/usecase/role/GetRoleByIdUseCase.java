package com.team.backend.usecase.role;

import com.team.backend.dto.response.RoleResponse;
import com.team.backend.entity.Role;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetRoleByIdUseCase {
  private final RoleRepository roleRepository;

  public RoleResponse execute(UUID roleId) {
    Role role = roleRepository.findById(roleId)
      .orElseThrow(() -> {
        log.warn("Role not found with id: {}", roleId);
        return new AppException(ErrorCode.NOT_FOUND, "Role not found");
      });

    return RoleResponse.from(role);
  }
}
