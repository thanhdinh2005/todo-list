package com.team.backend.service.impl;

import com.team.backend.entity.RefreshToken;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RefreshTokenRepository;
import com.team.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  @Value("${application.security.jwt.refresh-expiration-ms}")
  private Long refreshTokenDurationMs;

  public RefreshToken createRefreshToken(UUID userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User not found"));

    Instant expiryDate = Instant.now().plusSeconds(refreshTokenDurationMs);
    RefreshToken refreshToken = RefreshToken.issueFor(user, expiryDate);
    return refreshTokenRepository.save(refreshToken);
  }
}
