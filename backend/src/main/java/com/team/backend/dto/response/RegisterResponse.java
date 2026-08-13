package com.team.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RegisterResponse {
  private UUID id;
  private String email;
  private String fullName;
}
