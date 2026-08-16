package com.team.backend.dto.response;

import com.team.backend.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class RoleResponse {
  private UUID id;
  private String name;
  private String description;
  private Set<PermissionSummary> permissions;

  @Getter
  @Builder
  public static class PermissionSummary {
    private UUID id;
    private String code;
    private String description;
  }

  public static RoleResponse from(Role role) {
    return RoleResponse.builder()
      .id(role.getId())
      .name(role.getName())
      .description(role.getDescription())
      .permissions(role.getPermissions().stream()
        .map(p -> PermissionSummary.builder()
          .id(p.getId())
          .code(p.getName())
          .description(p.getDescription())
          .build())
        .collect(Collectors.toSet()))
      .build();
  }
}
