package com.team.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
  private UUID id;
  private String email;
  private String fullName;
  private boolean enabled;
  private List<String> roles;
  private Instant createdAt;
}
