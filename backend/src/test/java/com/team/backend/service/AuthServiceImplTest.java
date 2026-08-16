package com.team.backend.service;

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
import com.team.backend.service.impl.AuthServiceImpl;
import com.team.backend.service.impl.JwtService;
import com.team.backend.service.impl.RefreshTokenService;
import com.team.backend.utils.RoleTestFactory;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private RoleRepository roleRepository;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtService jwtService;
  @Mock private RefreshTokenService refreshTokenService;
  @Mock private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  private AuthServiceImpl authService;

  @Nested
  @DisplayName("register()")
  class Register {

    private RegisterRequest request;
    private Role userRole;

    @BeforeEach
    void setUp() {
      request = new RegisterRequest("test@test.com", "password123", "Test User");
      userRole = RoleTestFactory.userRole();
    }

    @Test
    @DisplayName("should create user when email is not taken")
    void shouldCreateUser_whenEmailNotTaken() {
      given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
      given(passwordEncoder.encode(request.getPassword())).willReturn("encoded-password");
      given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(userRole));
      given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

      RegisterResponse response = authService.register(request);

      assertThat(response.getEmail()).isEqualTo(request.getEmail());
      assertThat(response.getFullName()).isEqualTo(request.getFullName());

      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertThat(savedUser.getEmail()).isEqualTo(request.getEmail());

      verify(passwordEncoder).encode(request.getPassword());
    }

    @Test
    @DisplayName("should throw CONFLICT when email already exists")
    void shouldThrowConflict_whenEmailAlreadyExists() {
      given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

      assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(AppException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.CONFLICT);

      verify(roleRepository, never()).findByName(any());
      verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw NOT_FOUND when default role is missing")
    void shouldThrowNotFound_whenDefaultRoleMissing() {
      given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.empty());

      assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(AppException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.NOT_FOUND);

      verify(userRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("login()")
  class Login {

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

      LoginResponse response = authService.login(request);

      assertThat(response.getAccessToken()).isEqualTo("access-token-value");
      assertThat(response.getRefreshToken()).isEqualTo("refresh-token-value");
      assertThat(response.getExpiresIn()).isEqualTo(3600L);
    }

    @Test
    @DisplayName("should propagate exception when authentication fails")
    void shouldThrow_whenAuthenticationFails() {
      given(authenticationManager.authenticate(any()))
        .willThrow(new BadCredentialsException("Bad credentials"));

      assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(BadCredentialsException.class);
    }
  }

  @Nested
  @DisplayName("refreshToken()")
  class RefreshTokenTests {

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
      given(refreshTokenRepository.findByToken(TOKEN_VALUE)).willReturn(Optional.of(refreshToken));
      given(refreshToken.getUser()).willReturn(user);
      given(refreshToken.isValid()).willReturn(true);
      given(jwtService.generateAccessToken(user)).willReturn("new-access-token");
      given(jwtService.getExpirationSeconds()).willReturn(3600L);

      LoginResponse response = authService.refreshToken(new RefreshTokenRequest(TOKEN_VALUE));

      assertThat(response.getAccessToken()).isEqualTo("new-access-token");
    }

    @Test
    @DisplayName("should throw UNAUTHORIZED when token is expired or revoked")
    void shouldThrowUnauthorized_whenTokenExpiredOrRevoked() {
      given(refreshTokenRepository.findByToken(TOKEN_VALUE)).willReturn(Optional.of(refreshToken));
      given(refreshToken.getUser()).willReturn(user);
      given(refreshToken.isValid()).willReturn(false);

      assertThatThrownBy(() -> authService.refreshToken(new RefreshTokenRequest(TOKEN_VALUE)))
        .isInstanceOf(AppException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("should throw UNAUTHORIZED when token is not found")
    void shouldThrowUnauthorized_whenTokenNotFound() {
      given(refreshTokenRepository.findByToken("unknown-token")).willReturn(Optional.empty());

      assertThatThrownBy(() -> authService.refreshToken(new RefreshTokenRequest("unknown-token")))
        .isInstanceOf(AppException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.UNAUTHORIZED);
    }
  }

  @Nested
  @DisplayName("logout()")
  class Logout {

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
      given(refreshTokenRepository.findByToken(TOKEN_VALUE)).willReturn(Optional.of(refreshToken));
      given(refreshToken.getUser()).willReturn(user);

      authService.logout(new LogoutRequest(TOKEN_VALUE));

      verify(refreshToken).revoke();
    }

    @Test
    @DisplayName("should throw NOT_FOUND when token is missing")
    void shouldThrowNotFound_whenTokenMissing() {
      given(refreshTokenRepository.findByToken("missing")).willReturn(Optional.empty());

      assertThatThrownBy(() -> authService.logout(new LogoutRequest("missing")))
        .isInstanceOf(AppException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("getMe()")
  class GetMe {

    private UUID userId;
    private Role userRole;

    @BeforeEach
    void setUp() {
      userId = UUID.randomUUID();
      userRole = RoleTestFactory.userRole();
    }

    @Test
    @DisplayName("should return user when found")
    void shouldReturnUser_whenExists() {
      User user = UserTestFactory.create(userId, "test@test.com", "Test User", true, userRole);

      given(userRepository.findByIdWithRoles(userId)).willReturn(Optional.of(user));

      UserResponse response = authService.getMe(userId);

      assertThat(response.getEmail()).isEqualTo("test@test.com");
      assertThat(response.getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("should throw NOT_FOUND when user is missing")
    void shouldThrowNotFound_whenUserMissing() {
      given(userRepository.findByIdWithRoles(userId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> authService.getMe(userId))
        .isInstanceOf(AppException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.NOT_FOUND);
    }
  }
}
