package com.team.backend.usecase.auth;

import com.team.backend.dto.request.auth.RegisterRequest;
import com.team.backend.dto.response.RegisterResponse;
import com.team.backend.entity.Role;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RoleRepository;
import com.team.backend.repository.UserRepository;
import com.team.backend.utils.RoleTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RegisterUseCaseTest {
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private RoleRepository roleRepository;

  @InjectMocks
  private RegisterUseCase registerUseCase;

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

    RegisterResponse response = registerUseCase.register(request);

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

    assertThatThrownBy(() -> registerUseCase.register(request))
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

    assertThatThrownBy(() -> registerUseCase.register(request))
      .isInstanceOf(AppException.class)
      .extracting("errorCode")
      .isEqualTo(ErrorCode.NOT_FOUND);

    verify(userRepository, never()).save(any());
  }
}
