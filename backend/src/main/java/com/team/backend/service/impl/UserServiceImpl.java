package com.team.backend.service.impl;

import com.team.backend.dto.request.user.ChangePasswordRequest;
import com.team.backend.dto.request.user.UpdateUserRequest;
import com.team.backend.dto.request.user.UpdateUserRolesRequest;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.service.UserService;
import com.team.backend.usecase.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
  private final GetUsersUseCase getUsersUseCase;
  private final GetUserByIdUseCase getUserByIdUseCase;
  private final UpdateUserInfoUseCase updateUserInfoUseCase;
  private final UpdatePasswordUseCase updatePasswordUseCase;
  private final ChangeUserStatusUseCase changeUserStatusUseCase;
  private final UpdateRoleUserUseCase updateRoleUserUseCase;
  private final DeleteUserUseCase deleteUserUseCase;

  @Override
  public PageResponse<UserResponse> getUsers() {
    return getUsersUseCase.execute();
  }

  @Override
  public UserResponse getUserByID(UUID uuid) {
    return getUserByIdUseCase.execute(uuid);
  }

  @Override
  public UserResponse updateUserInfo(UpdateUserRequest request) {
    return updateUserInfoUseCase.execute(request);
  }

  @Override
  public void updatePassword(ChangePasswordRequest request) {
    updatePasswordUseCase.execute(request);
  }

  @Override
  public void changeUserStatus(boolean enabled) {
    changeUserStatusUseCase.execute(enabled);
  }

  @Override
  public UserResponse updateRoleUser(UpdateUserRolesRequest request) {
    return updateRoleUserUseCase.execute(request);
  }

  @Override
  public void deleteUser(UUID uuid) {
    deleteUserUseCase.execute(uuid);
  }
}
