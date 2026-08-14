package com.team.backend.usecase.user;

import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeleteUserUseCase {
  private final UserRepository userRepository;

  public void execute(UUID targetId, UUID currentUserId) {

    if (targetId.equals(currentUserId)) {
      throw new AppException(
        ErrorCode.BAD_REQUEST,
        "You cannot delete yourself"
      );
    }

    User user = userRepository.findById(targetId)
      .orElseThrow(() -> new AppException(
        ErrorCode.NOT_FOUND,
        "User not found with id: " + targetId
      ));

    user.disable();
  }
}
