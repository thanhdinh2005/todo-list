package com.team.backend.usecase.auth;

import com.team.backend.dto.request.auth.LogoutRequest;
import com.team.backend.entity.RefreshToken;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutUseCase {
  private final RefreshTokenRepository refreshTokenRepository;

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
}
