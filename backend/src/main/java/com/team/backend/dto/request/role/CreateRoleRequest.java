package com.team.backend.dto.request.role;

import java.util.List;

public class CreateRoleRequest {
  private String name;
  private String description;
  private List<Integer> permissionIds;
}
