package com.team.backend.dto.request.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateRoleRequest {

  @NotBlank(message = "Role name is required")
  @Size(max = 100, message = "Role name cannot exceed 100 characters")
  private String name;
  private String description;
  private Set<UUID> permissionIds;
}
