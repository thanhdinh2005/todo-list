package com.team.backend.controller;

import com.team.backend.common.AppResponse;
import com.team.backend.common.RateLimit;
import com.team.backend.dto.request.user.ChangePasswordRequest;
import com.team.backend.dto.request.user.UpdateUserRequest;
import com.team.backend.dto.request.user.UpdateUserRolesRequest;
import com.team.backend.dto.request.user.UserFilterParam;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.security.CustomUserDetails;
import com.team.backend.usecase.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@RateLimit
public class UserController {
  private final GetUsersUseCase getUsersUseCase;
  private final GetUserByIdUseCase getUserByIdUseCase;
  private final UpdateUserInfoUseCase updateUserInfoUseCase;
  private final ChangePasswordUseCase changePasswordUseCase;
  private final ChangeUserStatusUseCase changeUserStatusUseCase;
  private final UpdateRoleUserUseCase updateRoleUserUseCase;
  private final DeleteUserUseCase deleteUserUseCase;

  @GetMapping
  public ResponseEntity<PageResponse<UserResponse>> getUsers(
    @ModelAttribute UserFilterParam param
    ) {
    return ResponseEntity.ok(getUsersUseCase.execute(param));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AppResponse<UserResponse>> getUserById(
    @PathVariable UUID id
    ) {
    return ResponseEntity.ok(
      AppResponse.success(getUserByIdUseCase.execute(id))
    );
  }

  @PutMapping("/update-profile")
  public ResponseEntity<AppResponse<UserResponse>> updateUserProfile(
    @AuthenticationPrincipal CustomUserDetails currentUser,
    @RequestBody @Valid UpdateUserRequest request
    ) {
    return ResponseEntity.ok(
      AppResponse.success(updateUserInfoUseCase.execute(request, currentUser.getId()))
    );
  }

  @PatchMapping("/change-password")
  public ResponseEntity<AppResponse<Void>> changePassword(
    @RequestBody @Valid ChangePasswordRequest request,
    @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
    changePasswordUseCase.execute(request, currentUser.getId());
    return ResponseEntity.ok(
      AppResponse.success(null)
    );
  }

  @PatchMapping("/{id}/enable")
  public ResponseEntity<AppResponse<Void>> enableUser(
    @PathVariable UUID id,
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    changeUserStatusUseCase.execute(true, id, currentUser.getId());
    return ResponseEntity.ok(
      AppResponse.success(null)
    );
  }

  @PatchMapping("/{id}/disable")
  public ResponseEntity<AppResponse<Void>> disableUser(
    @PathVariable UUID id,
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    changeUserStatusUseCase.execute(false, id, currentUser.getId());
    return ResponseEntity.ok(
      AppResponse.success(null)
    );
  }

  @PatchMapping("/{id}/roles")
  public ResponseEntity<AppResponse<UserResponse>> updateRolesUser(
    @PathVariable UUID id,
    @RequestBody @Valid UpdateUserRolesRequest request,
    @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
    return ResponseEntity.ok(
      AppResponse.success(updateRoleUserUseCase.execute(request, id, currentUser.getId()))
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<AppResponse<Void>> deleteUser(
    @PathVariable UUID id,
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    deleteUserUseCase.execute(id, currentUser.getId());
    return ResponseEntity.ok(
      AppResponse.success(null)
    );
  }
}
