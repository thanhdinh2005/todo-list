package com.team.backend.usecase.permission;

import com.team.backend.dto.response.PermissionResponse;
import com.team.backend.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetAllPermissionsUseCase {
  private final PermissionRepository permissionRepository;

  public List<PermissionResponse> execute() {
    return permissionRepository.findAll().stream()
      .map(PermissionResponse::from)
      .toList();
  }
}
