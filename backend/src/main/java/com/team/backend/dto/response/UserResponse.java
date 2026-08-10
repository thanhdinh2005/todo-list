package com.team.backend.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UserResponse {
  private UUID id;
  private String email;
  private String fullName;
  private boolean enabled;
  private List<String> roles;
  private Instant createdAt;
}
