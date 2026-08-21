package com.team.backend.usecase.auth;

import com.team.backend.dto.request.auth.RefreshTokenRequest;
import com.team.backend.dto.response.LoginResponse;
import com.team.backend.entity.RefreshToken;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RefreshTokenRepository;
import com.team.backend.service.JwtService;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenUseCaseTest {
  @Mock private RefreshTokenRepository refreshTokenRepository;
  @Mock private JwtService jwtService;

  @InjectMocks
  private RefreshTokenUseCase refreshTokenUseCase;

  private static final String TOKEN_VALUE = "valid-refresh-token";

  private User user;
  private RefreshToken refreshToken;

  @BeforeEach
  void setUp() {
    user = UserTestFactory.createDefault();
    refreshToken = mock(RefreshToken.class);
  }

  @Test
  @DisplayName("should return new access token when refresh token is valid")
  void shouldReturnNewAccessToken_whenTokenValid() {
    given(refreshTokenRepository.findByToken(TOKEN_VALUE))
      .willReturn(Optional.of(refreshToken));
    given(refreshToken.getUser()).willReturn(user);
    given(refreshToken.isValid()).willReturn(true);
    given(jwtService.generateAccessToken(user))
      .willReturn("new-access-token");
    given(jwtService.getExpirationSeconds()).willReturn(3600L);

    LoginResponse response = refreshTokenUseCase.refreshToken(
      new RefreshTokenRequest(TOKEN_VALUE)
    );

    assertThat(response.getAccessToken()).isEqualTo("new-access-token");
  }

  @Test
  @DisplayName("should throw UNAUTHORIZED when token is expired or revoked")
  void shouldThrowUnauthorized_whenTokenExpiredOrRevoked() {
    given(refreshTokenRepository.findByToken(TOKEN_VALUE))
      .willReturn(Optional.of(refreshToken));
    given(refreshToken.getUser()).willReturn(user);
    given(refreshToken.isValid()).willReturn(false);

    assertThatThrownBy(() -> refreshTokenUseCase.refreshToken(
      new RefreshTokenRequest(TOKEN_VALUE))
    )
      .isInstanceOf(AppException.class)
      .extracting("errorCode")
      .isEqualTo(ErrorCode.UNAUTHORIZED);
  }

  @Test
  @DisplayName("should throw UNAUTHORIZED when token is not found")
  void shouldThrowUnauthorized_whenTokenNotFound() {
    given(refreshTokenRepository.findByToken("unknown-token"))
      .willReturn(Optional.empty());

    assertThatThrownBy(() -> refreshTokenUseCase.refreshToken(
      new RefreshTokenRequest("unknown-token"))
    )
      .isInstanceOf(AppException.class)
      .extracting("errorCode")
      .isEqualTo(ErrorCode.UNAUTHORIZED);
  }
}
