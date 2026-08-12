package com.team.backend.service.impl;

import com.team.backend.dto.request.auth.LoginRequest;
import com.team.backend.dto.request.auth.LogoutRequest;
import com.team.backend.dto.request.auth.RefreshTokenRequest;
import com.team.backend.dto.request.auth.RegisterRequest;
import com.team.backend.dto.response.LoginResponse;
import com.team.backend.dto.response.RegisterResponse;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.repository.UserRepository;
import com.team.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;

  @Override
  public RegisterResponse register(RegisterRequest request) {


    return null;
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    return null;
  }

  @Override
  public LoginResponse refreshToken(RefreshTokenRequest refreshToken) {
    return null;
  }

  @Override
  public void logout(LogoutRequest refreshToken) {

  }

  @Override
  public UserResponse getMe(UUID currentUserId) {
    return null;
  }
}
