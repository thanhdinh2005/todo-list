package com.team.backend.controller;

import com.team.backend.common.AppResponse;
import com.team.backend.dto.response.PermissionResponse;
import com.team.backend.usecase.permission.GetAllPermissionsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('role:manage')")
public class PermissionController {
  private final GetAllPermissionsUseCase getAllPermissionsUseCase;

  @GetMapping
  public ResponseEntity<AppResponse<List<PermissionResponse>>> getAllPermissions() {
    var result = getAllPermissionsUseCase.execute();
    return ResponseEntity.ok(
      AppResponse.success(200, "Get permissions successfully", result)
    );
  }
}
