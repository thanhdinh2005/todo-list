package com.team.backend.usecase.auth;

import com.team.backend.dto.request.auth.RefreshTokenRequest;
import com.team.backend.dto.response.LoginResponse;
import com.team.backend.entity.RefreshToken;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RefreshTokenRepository;
import com.team.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUseCase {
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtService jwtService;

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
}
