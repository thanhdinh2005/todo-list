package com.team.backend.usecase.role;

import com.team.backend.entity.Role;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RoleRepository;
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
public class DeleteRoleUseCase {
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  public void execute(UUID roleId) {
    Role role = roleRepository.findById(roleId)
      .orElseThrow(() -> {
        log.warn("Role not found with id: {}", roleId);
        return new AppException(ErrorCode.NOT_FOUND, "Role not found");
      });

    if (userRepository.existsByRolesId(roleId)) {
      throw new AppException(ErrorCode.CONFLICT, "Không thể xóa role đang được gán cho user");
    }

    roleRepository.delete(role);
    log.info("Deleted role successfully with id: {}", roleId);
  }
}
