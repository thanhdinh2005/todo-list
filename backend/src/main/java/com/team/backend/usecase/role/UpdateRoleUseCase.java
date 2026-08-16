package com.team.backend.usecase.role;

import com.team.backend.dto.request.role.UpdateRoleRequest;
import com.team.backend.dto.response.RoleResponse;
import com.team.backend.entity.Permission;
import com.team.backend.entity.Role;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.PermissionRepository;
import com.team.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateRoleUseCase {
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  public RoleResponse execute(UUID roleId, UpdateRoleRequest request) {
    Role role = roleRepository.findById(roleId)
      .orElseThrow(() -> {
        log.warn("Role not found with id: {}", roleId);
        return new AppException(ErrorCode.NOT_FOUND, "Role not found");
      });

    if (request.getName() != null && !request.getName().isBlank()
      && !request.getName().equals(role.getName())) {
      if (roleRepository.existsByName(request.getName())) {
        throw new AppException(ErrorCode.CONFLICT, "Role name already exists");
      }
      role.rename(request.getName());
    }

    if (request.getDescription() != null) {
      role.updateDescription(request.getDescription());
    }

    if (request.getPermissionIds() != null) {
      Set<Permission> permissions = resolvePermissions(request.getPermissionIds());
      role.replacePermissions(permissions);
    }

    log.info("Updated role successfully with id: {}", role.getId());
    return RoleResponse.from(role);
  }

  private Set<Permission> resolvePermissions(Set<UUID> permissionIds) {
    if (permissionIds.isEmpty()) {
      return Set.of();
    }
    List<Permission> found = permissionRepository.findAllByIdIn(permissionIds);
    if (found.size() != permissionIds.size()) {
      Set<UUID> foundIds = found.stream().map(Permission::getId).collect(Collectors.toSet());
      Set<UUID> missingIds = new HashSet<>(permissionIds);
      missingIds.removeAll(foundIds);
      throw new AppException(ErrorCode.NOT_FOUND, "Permission(s) not found: " + missingIds);
    }
    return new HashSet<>(found);
  }
}
