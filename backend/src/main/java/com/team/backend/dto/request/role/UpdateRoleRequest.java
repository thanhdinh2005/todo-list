package com.team.backend.dto.request.role;

import java.util.List;

public class UpdateRoleRequest {
  private String name;
  private String description;
  private List<Integer> permissionIds;
}
