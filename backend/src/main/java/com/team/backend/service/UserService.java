package com.team.backend.service;

import com.team.backend.dto.request.user.ChangePasswordRequest;
import com.team.backend.dto.request.user.UpdateUserRequest;
import com.team.backend.dto.request.user.UpdateUserRolesRequest;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
  PageResponse<UserResponse> getUsers();
  UserResponse getUserByID(UUID uuid);
  UserResponse updateUserInfo(UpdateUserRequest request);
  void updatePassword(ChangePasswordRequest request);
  void changeUserStatus(boolean enabled);
  UserResponse updateRoleUser(UpdateUserRolesRequest request);
  void deleteUser(UUID uuid);
}
