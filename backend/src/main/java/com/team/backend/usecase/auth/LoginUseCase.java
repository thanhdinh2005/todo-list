package com.team.backend.usecase.auth;

import com.team.backend.dto.request.auth.LoginRequest;
import com.team.backend.dto.response.LoginResponse;
import com.team.backend.entity.RefreshToken;
import com.team.backend.entity.User;
import com.team.backend.security.CustomUserDetails;
import com.team.backend.service.JwtService;
import com.team.backend.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUseCase {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

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
}
