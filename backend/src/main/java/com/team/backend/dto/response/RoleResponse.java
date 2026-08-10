package com.team.backend.dto.response;

import java.util.List;
import java.util.UUID;

public class RoleResponse {
  private UUID id;
  private String name;
  private String description;
  private List<String> permissions;

}
