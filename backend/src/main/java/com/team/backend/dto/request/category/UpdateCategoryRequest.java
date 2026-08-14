package com.team.backend.dto.request.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCategoryRequest {
  private String name;
  private String colorCode;
}
