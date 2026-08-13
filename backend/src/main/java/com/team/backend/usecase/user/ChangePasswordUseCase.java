package com.team.backend.usecase.user;

import com.team.backend.dto.request.user.ChangePasswordRequest;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChangePasswordUseCase {
  private final UserRepository userRepository;
  private final PasswordEncoder encoder;


  public void execute(ChangePasswordRequest request, UUID userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> {
        log.warn("User not found with id: {}", userId);
        return new AppException(ErrorCode.NOT_FOUND, "User not found with id: " + userId);
      });

    user.changePassword(request.getOldPassword(), request.getNewPassword(), encoder);
    log.info("Change password successfully with uid: {}", userId);
  }
}
