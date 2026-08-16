package com.team.backend.dto.response;

import com.team.backend.entity.Permission;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PermissionResponse {
  private UUID id;
  private String code;
  private String description;

  public static PermissionResponse from(Permission permission) {
    return PermissionResponse.builder()
      .id(permission.getId())
      .code(permission.getName())
      .description(permission.getDescription())
      .build();
  }
}
