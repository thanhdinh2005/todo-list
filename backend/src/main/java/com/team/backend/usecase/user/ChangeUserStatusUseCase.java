package com.team.backend.usecase.user;

import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChangeUserStatusUseCase {
  private final UserRepository userRepository;

  public void execute(boolean enabled, UUID userId, UUID currentUserId) {
    if (userId.equals(currentUserId)) {
      throw new AppException(
        ErrorCode.BAD_REQUEST,
        "You cannot change your own status"
      );
    }

    User user = userRepository.findById(userId)
      .orElseThrow(() -> {
        log.warn("User not found with id: {}", userId);
        return new AppException(ErrorCode.NOT_FOUND, "User not found with id: " + userId);
      });

    if (enabled) user.enabled();
    else user.disable();
    log.info("Change status successfully, new status: {}", user.isEnabled());
  }
}
