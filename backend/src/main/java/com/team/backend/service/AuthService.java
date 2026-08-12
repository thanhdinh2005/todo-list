package com.team.backend.service;

import com.team.backend.dto.request.auth.LoginRequest;
import com.team.backend.dto.request.auth.LogoutRequest;
import com.team.backend.dto.request.auth.RefreshTokenRequest;
import com.team.backend.dto.request.auth.RegisterRequest;
import com.team.backend.dto.response.LoginResponse;
import com.team.backend.dto.response.RegisterResponse;
import com.team.backend.dto.response.UserResponse;

import java.util.UUID;

public interface AuthService {
  RegisterResponse register(RegisterRequest request);
  LoginResponse login(LoginRequest request);
  LoginResponse refreshToken(RefreshTokenRequest refreshToken);
  void logout(LogoutRequest refreshToken);
  UserResponse getMe(UUID currentUserId);
}
