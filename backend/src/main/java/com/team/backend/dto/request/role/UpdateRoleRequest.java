package com.team.backend.dto.request.role;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UpdateRoleRequest {
  private String name;
  private String description;
  private Set<UUID> permissionIds;
}
