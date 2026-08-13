package com.team.backend.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateUserRolesRequest {
  @NotNull(message = "List can't be null")
  private List<UUID> roleIds;
}
