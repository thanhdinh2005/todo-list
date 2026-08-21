package com.team.backend.usecase.auth;

import com.team.backend.dto.request.auth.LoginRequest;
import com.team.backend.dto.response.LoginResponse;
import com.team.backend.entity.RefreshToken;
import com.team.backend.entity.User;
import com.team.backend.security.CustomUserDetails;
import com.team.backend.service.JwtService;
import com.team.backend.service.RefreshTokenService;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class LoginUseCaseTest {
  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtService jwtService;
  @Mock private RefreshTokenService refreshTokenService;

  @InjectMocks
  private LoginUseCase loginUseCase;

  private LoginRequest request;
  private User user;

  @BeforeEach
  void setUp() {
    request = new LoginRequest("test@test.com", "password123");
    user = UserTestFactory.createDefault();
  }

  @Test
  @DisplayName("should return tokens when credentials are valid")
  void shouldReturnTokens_whenCredentialsValid() {
    CustomUserDetails userDetails = new CustomUserDetails(user, null);
    Authentication authentication = mock(Authentication.class);
    RefreshToken refreshToken = mock(RefreshToken.class);

    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
      .willReturn(authentication);
    given(authentication.getPrincipal()).willReturn(userDetails);
    given(refreshToken.getToken()).willReturn("refresh-token-value");
    given(jwtService.generateAccessToken(user)).willReturn("access-token-value");
    given(jwtService.getExpirationSeconds()).willReturn(3600L);
    given(refreshTokenService.createRefreshToken(user.getId())).willReturn(refreshToken);

    LoginResponse response = loginUseCase.login(request);

    assertThat(response.getAccessToken()).isEqualTo("access-token-value");
    assertThat(response.getRefreshToken()).isEqualTo("refresh-token-value");
    assertThat(response.getExpiresIn()).isEqualTo(3600L);
  }

  @Test
  @DisplayName("should propagate exception when authentication fails")
  void shouldThrow_whenAuthenticationFails() {
    given(authenticationManager.authenticate(any()))
      .willThrow(new BadCredentialsException("Bad credentials"));

    assertThatThrownBy(() -> loginUseCase.login(request))
      .isInstanceOf(BadCredentialsException.class);
  }
}
