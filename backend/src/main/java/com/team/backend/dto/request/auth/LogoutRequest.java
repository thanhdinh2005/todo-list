package com.team.backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogoutRequest {
  @NotBlank(message = "Refresh Token is required")
  private String refreshToken;
}
