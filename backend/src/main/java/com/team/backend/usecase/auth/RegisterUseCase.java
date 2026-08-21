package com.team.backend.usecase.auth;

import com.team.backend.dto.request.auth.RegisterRequest;
import com.team.backend.dto.response.RegisterResponse;
import com.team.backend.entity.Role;
import com.team.backend.entity.User;
import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import com.team.backend.repository.RoleRepository;
import com.team.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterUseCase {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

  public RegisterResponse register(RegisterRequest request) {
    log.info("Registering new user");
    if (userRepository.existsByEmail(request.getEmail())) {
      log.warn("Resgistration failed: email already exists");
      throw new AppException(ErrorCode.CONFLICT, "Email: " + request.getEmail() + " already exists");
    }

    User user = User.create(
      request.getEmail(), request.getPassword(), request.getFullName(), passwordEncoder
    );

    Role role = roleRepository.findByName("ROLE_USER")
      .orElseThrow(() -> {
        log.error("Registration failed: default role ROLE_USER not found");
        return new AppException(ErrorCode.NOT_FOUND, "Role name not found");
      });

    user.assignRole(role);

    User savedUser = userRepository.save(user);

    log.info("User registered successfully, userId={}", savedUser.getId());

    return RegisterResponse.builder()
      .email(savedUser.getEmail())
      .fullName(savedUser.getFullName())
      .id(savedUser.getId())
      .build();
  }
}
