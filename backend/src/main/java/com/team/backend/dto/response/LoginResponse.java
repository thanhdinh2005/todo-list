package com.team.backend.dto.response;

import java.time.Instant;

public class LoginResponse {
  private String accessToken;
  private String refreshToken;
  private Instant expiresIn;
}
