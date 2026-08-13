package com.team.backend.controller;

import com.team.backend.common.AppResponse;
import com.team.backend.dto.request.auth.LoginRequest;
import com.team.backend.dto.request.auth.LogoutRequest;
import com.team.backend.dto.request.auth.RefreshTokenRequest;
import com.team.backend.dto.request.auth.RegisterRequest;
import com.team.backend.dto.response.LoginResponse;
import com.team.backend.dto.response.RegisterResponse;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.entity.User;
import com.team.backend.security.CustomUserDetails;
import com.team.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AppResponse<RegisterResponse>> register(
    @RequestBody @Valid RegisterRequest request
  ) {
    RegisterResponse response = authService.register(request);

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(
        AppResponse.success(
          HttpStatus.CREATED.value(),
          "Registration successful",
          response
        )
      );
  }

  @PostMapping("/login")
  public ResponseEntity<AppResponse<LoginResponse>> login(
    @RequestBody @Valid LoginRequest request
  ) {
    LoginResponse response = authService.login(request);

    return ResponseEntity.ok(AppResponse.success(response));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AppResponse<LoginResponse>> refresh(
    @RequestBody @Valid RefreshTokenRequest request
  ) {
    LoginResponse response =
      authService.refreshToken(request);

    return ResponseEntity.ok(AppResponse.success(response));
  }

  @PostMapping("/logout")
  public ResponseEntity<AppResponse<Void>> logout(
    @RequestBody @Valid LogoutRequest request
  ) {
    authService.logout(request);

    return ResponseEntity.ok(AppResponse.success(null));
  }

  @GetMapping("/me")
  public ResponseEntity<AppResponse<UserResponse>> getMe(
    @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
    return ResponseEntity.ok(AppResponse.success(authService.getMe(currentUser.getId())));
  }
}
