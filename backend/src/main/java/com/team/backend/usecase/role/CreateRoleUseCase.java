package com.team.backend.usecase.role;

import com.team.backend.dto.request.role.CreateRoleRequest;
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
public class CreateRoleUseCase {
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  public RoleResponse execute(CreateRoleRequest request) {
    if (roleRepository.existsByName(request.getName())) {
      throw new AppException(ErrorCode.CONFLICT, "Role name already exists");
    }

    Role role = Role.create(request.getName(), request.getDescription());

    if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
      Set<Permission> permissions = resolvePermissions(request.getPermissionIds());
      role.grantPermissions(permissions);
    }

    role = roleRepository.save(role);
    log.info("Created role successfully with id: {}", role.getId());

    return RoleResponse.from(role);
  }

  private Set<Permission> resolvePermissions(Set<UUID> permissionIds) {
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
