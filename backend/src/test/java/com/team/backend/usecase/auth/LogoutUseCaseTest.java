package com.team.backend.usecase.auth;

import com.team.backend.dto.request.auth.LogoutRequest;
import com.team.backend.entity.RefreshToken;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RefreshTokenRepository;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LogoutUseCaseTest {
  @Mock private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  private LogoutUseCase logoutUseCase;

  private static final String TOKEN_VALUE = "token";

  private User user;

  @BeforeEach
  void setUp() {
    user = UserTestFactory.createDefault();
  }

  @Test
  @DisplayName("should revoke token when it exists")
  void shouldRevokeToken_whenTokenExists() {
    RefreshToken refreshToken = mock(RefreshToken.class);
    given(refreshTokenRepository.findByToken(TOKEN_VALUE))
      .willReturn(Optional.of(refreshToken));
    given(refreshToken.getUser()).willReturn(user);

    logoutUseCase.logout(new LogoutRequest(TOKEN_VALUE));

    verify(refreshToken).revoke();
  }

  @Test
  @DisplayName("should throw NOT_FOUND when token is missing")
  void shouldThrowNotFound_whenTokenMissing() {
    given(refreshTokenRepository.findByToken("missing"))
      .willReturn(Optional.empty());

    assertThatThrownBy(() -> logoutUseCase.logout(
      new LogoutRequest("missing"))
    )
      .isInstanceOf(AppException.class)
      .extracting("errorCode")
      .isEqualTo(ErrorCode.NOT_FOUND);
  }
}
