package com.team.backend.service.impl;

import com.team.backend.dto.request.auth.LoginRequest;
import com.team.backend.dto.request.auth.LogoutRequest;
import com.team.backend.dto.request.auth.RefreshTokenRequest;
import com.team.backend.dto.request.auth.RegisterRequest;
import com.team.backend.dto.response.LoginResponse;
import com.team.backend.dto.response.RegisterResponse;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.entity.RefreshToken;
import com.team.backend.entity.Role;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RefreshTokenRepository;
import com.team.backend.repository.RoleRepository;
import com.team.backend.repository.UserRepository;
import com.team.backend.security.CustomUserDetails;
import com.team.backend.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public RegisterResponse register(RegisterRequest request) {
    log.info("Registering new user");
    if (userRepository.existsByEmail(request.getEmail())) {
      log.warn("Resgistration failed: email already exists");
      throw new AppException(ErrorCode.CONFLICT, "Email: " + request.getEmail() + " already exists");
    }

    User user = User.create(
      request.getEmail(), request.getPassword(), request.getFullName(), passwordEncoder
    );

    Role role = roleRepository.findByName("ROLE_USER")
      .orElseThrow(() -> {
        log.error("Registration failed: default role ROLE_USER not found");
        return new AppException(ErrorCode.NOT_FOUND, "Role name not found");
      });

    user.assignRole(role);

    User savedUser = userRepository.save(user);

    log.info("User registered successfully, userId={}", savedUser.getId());

    return RegisterResponse.builder()
      .email(savedUser.getEmail())
      .fullName(savedUser.getFullName())
      .id(savedUser.getId())
      .build();
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    Authentication authentication =
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          request.getEmail(),
          request.getPassword()
        )
      );

    CustomUserDetails userDetails =
      (CustomUserDetails) authentication.getPrincipal();

    User user = userDetails.getUser();

    String accessToken = jwtService.generateAccessToken(user);

    RefreshToken refreshToken =
      refreshTokenService.createRefreshToken(user.getId());

    log.info("User logged in successfully, userId={}", user.getId());

    return LoginResponse.builder()
      .accessToken(accessToken)
      .expiresIn(jwtService.getExpirationSeconds())
      .refreshToken(refreshToken.getToken())
      .build();
  }

  @Override
  public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

    RefreshToken refreshToken = refreshTokenRepository
      .findByToken(refreshTokenRequest.getRefreshToken())
      .orElseThrow(() -> {
        log.warn("Refresh token not found");

        return new AppException(
          ErrorCode.UNAUTHORIZED,
          "Invalid refresh token. Please login again"
        );
      });

    if (!refreshToken.isValid()) {
      log.warn(
        "Refresh token is expired or revoked, userId={}",
        refreshToken.getUser().getId()
      );

      throw new AppException(
        ErrorCode.UNAUTHORIZED,
        "Refresh token is no longer valid. Please login again"
      );
    }

    String newAccessToken = jwtService.generateAccessToken(refreshToken.getUser());

    log.info(
      "Access token refreshed successfully, userId={}",
      refreshToken.getUser().getId()
    );

    return LoginResponse.builder()
      .refreshToken(refreshToken.getToken())
      .accessToken(newAccessToken)
      .expiresIn(jwtService.getExpirationSeconds())
      .build();
  }

  @Override
  @Transactional
  public void logout(LogoutRequest logoutRequest) {
    RefreshToken refreshToken = refreshTokenRepository
      .findByToken(logoutRequest.getRefreshToken())
      .orElseThrow(() -> new AppException(
        ErrorCode.NOT_FOUND,
        "Refresh token not found"
      ));

    refreshToken.revoke();

    log.info(
      "User logged out successfully, userId={}",
      refreshToken.getUser().getId()
    );
  }

  @Override
  public UserResponse getMe(UUID currentUserId) {
    User user = userRepository.findByIdWithRoles(currentUserId)
      .orElseThrow( () -> {
        log.warn("User not found");
        return new AppException(ErrorCode.NOT_FOUND, "User not found");
      });

    return UserResponse.builder()
      .id(user.getId())
      .email(user.getEmail())
      .enabled(user.isEnabled())
      .fullName(user.getFullName())
      .roles(user.getRoles().stream().map(Role::getName).toList())
      .createdAt(user.getCreatedAt())
      .build();
  }
}
