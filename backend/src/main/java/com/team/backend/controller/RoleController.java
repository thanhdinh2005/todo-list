package com.team.backend.controller;

import com.team.backend.common.AppResponse;
import com.team.backend.common.RateLimit;
import com.team.backend.dto.request.role.CreateRoleRequest;
import com.team.backend.dto.request.role.UpdateRoleRequest;
import com.team.backend.dto.response.RoleResponse;
import com.team.backend.usecase.role.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@RateLimit
@PreAuthorize("hasAuthority('role:manage')")
public class RoleController {
  private final GetAllRolesUseCase getAllRolesUseCase;
  private final GetRoleByIdUseCase getRoleByIdUseCase;
  private final CreateRoleUseCase createRoleUseCase;
  private final UpdateRoleUseCase updateRoleUseCase;
  private final DeleteRoleUseCase deleteRoleUseCase;

  @GetMapping
  public ResponseEntity<AppResponse<List<RoleResponse>>> getAllRoles() {
    List<RoleResponse> roles = getAllRolesUseCase.execute();
    return ResponseEntity.ok(
      AppResponse.success(200, "Get roles successfully", roles)
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<AppResponse<RoleResponse>> getRoleById(
    @PathVariable UUID id
  ) {
    RoleResponse role = getRoleByIdUseCase.execute(id);
    return ResponseEntity.ok(
      AppResponse.success(200, "Get role successfully", role)
    );
  }

  @PostMapping
  public ResponseEntity<AppResponse<RoleResponse>> createRole(
    @RequestBody @Valid CreateRoleRequest request
  ) {
    RoleResponse role = createRoleUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(
      AppResponse.success(HttpStatus.CREATED.value(), "Create role successfully", role)
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<AppResponse<RoleResponse>> updateRole(
    @PathVariable UUID id,
    @RequestBody @Valid UpdateRoleRequest request
  ) {
    RoleResponse role = updateRoleUseCase.execute(id, request);
    return ResponseEntity.ok(AppResponse.success(200, "Update role successfully", role));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<AppResponse<Void>> deleteRole(@PathVariable UUID id) {
    deleteRoleUseCase.execute(id);
    return ResponseEntity.ok(AppResponse.success(200, "Delete role successfully", null));
  }
}
