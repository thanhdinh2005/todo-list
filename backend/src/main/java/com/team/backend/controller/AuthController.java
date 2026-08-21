package com.team.backend.controller;

import com.team.backend.apispec.AuthApiSpec;
import com.team.backend.common.RateLimit;
import com.team.backend.dto.request.auth.*;
import com.team.backend.common.AppResponse;
import com.team.backend.dto.response.LoginResponse;
import com.team.backend.dto.response.RegisterResponse;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.security.CustomUserDetails;
import com.team.backend.usecase.auth.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@RateLimit
public class AuthController implements AuthApiSpec {
  private final LoginUseCase loginUseCase;
  private final LogoutUseCase logoutUseCase;
  private final RegisterUseCase registerUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;
  private final GetUserProfileUseCase getUserProfileUseCase;

  @Override
  @PostMapping("/register")
  public ResponseEntity<AppResponse<RegisterResponse>> register(
    @RequestBody @Valid RegisterRequest request
  ) {
    RegisterResponse response = registerUseCase.register(request);

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(AppResponse.success(
        HttpStatus.CREATED.value(), "Registration successful", response)
      );
  }

  @Override
  @PostMapping("/login")
  public ResponseEntity<AppResponse<LoginResponse>> login(
    @RequestBody @Valid LoginRequest request
  ) {
    LoginResponse response = loginUseCase.login(request);
    return ResponseEntity.ok(AppResponse.success(response));
  }

  @Override
  @PostMapping("/refresh")
  public ResponseEntity<AppResponse<LoginResponse>> refresh(
    @RequestBody @Valid RefreshTokenRequest request
  ) {
    LoginResponse response = refreshTokenUseCase.refreshToken(request);
    return ResponseEntity.ok(AppResponse.success(response));
  }

  @Override
  @PostMapping("/logout")
  public ResponseEntity<AppResponse<Void>> logout(
    @RequestBody @Valid LogoutRequest request
  ) {
    logoutUseCase.logout(request);
    return ResponseEntity.ok(AppResponse.success(null));
  }

  @Override
  @GetMapping("/me")
  public ResponseEntity<AppResponse<UserResponse>> getMe(
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    return ResponseEntity.ok(
      AppResponse.success(getUserProfileUseCase.getMe(currentUser.getId()))
    );
  }
}
