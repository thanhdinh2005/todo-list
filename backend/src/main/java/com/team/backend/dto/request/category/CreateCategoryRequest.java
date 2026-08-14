package com.team.backend.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateCategoryRequest {
  @NotBlank(message = "Category name is required")
  @Size(max = 100, message = "Category name must not exceed 100 characters")
  private String name;

  private String colorCode;
}
