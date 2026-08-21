package com.team.backend.usecase.auth;

import com.team.backend.dto.response.UserResponse;
import com.team.backend.entity.Role;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.UserRepository;
import com.team.backend.utils.RoleTestFactory;
import com.team.backend.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GetUserProfileUseCaseTest {
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private GetUserProfileUseCase getUserProfileUseCase;

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
    User user = UserTestFactory
      .create(
        userId,
        "test@test.com",
        "Test User",
        true,
        userRole
      );

    given(userRepository.findByIdWithRoles(userId))
      .willReturn(Optional.of(user));

    UserResponse response = getUserProfileUseCase.getMe(userId);

    assertThat(response.getEmail()).isEqualTo("test@test.com");
    assertThat(response.getRoles()).containsExactly("ROLE_USER");
  }

  @Test
  @DisplayName("should throw NOT_FOUND when user is missing")
  void shouldThrowNotFound_whenUserMissing() {
    given(userRepository.findByIdWithRoles(userId))
      .willReturn(Optional.empty());

    assertThatThrownBy(() -> getUserProfileUseCase.getMe(userId))
      .isInstanceOf(AppException.class)
      .extracting("errorCode")
      .isEqualTo(ErrorCode.NOT_FOUND);
  }
}
