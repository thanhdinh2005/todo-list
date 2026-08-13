package com.team.backend.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserRequest {
  @NotBlank(message = "Full name is required")
  @Size(max = 100, message = "Full name must not exceed 100 characters")
  private String fullName;
}
