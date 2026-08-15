package com.team.backend.dto.request.user;

import com.team.backend.dto.request.BasePageRequest;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserFilterParam extends BasePageRequest {
  private Boolean enabled;
}
