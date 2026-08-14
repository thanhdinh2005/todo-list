package com.team.backend.controller;

import com.team.backend.common.AppResponse;
import com.team.backend.dto.request.category.CreateCategoryRequest;
import com.team.backend.dto.request.category.UpdateCategoryRequest;
import com.team.backend.dto.response.CategoryResponse;
import com.team.backend.security.CustomUserDetails;
import com.team.backend.usecase.category.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
  private final GetAllCategoryUseCase getAllCategoryUseCase;
  private final CreateCategoryUseCase createCategoryUseCase;
  private final UpdateCategoryUseCase updateCategoryUseCase;
  private final DeleteCategoryUseCase deleteCategoryUseCase;

  @GetMapping
  public ResponseEntity<AppResponse<List<CategoryResponse>>> getAllCategories(
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    return ResponseEntity.ok(
      AppResponse.success(getAllCategoryUseCase.execute(currentUser.getId()))
    );
  }

  @PostMapping
  public ResponseEntity<AppResponse<CategoryResponse>> createCategory(
    @RequestBody @Valid CreateCategoryRequest request,
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
      AppResponse.success(
        201,
        "Create Category successfully",
        createCategoryUseCase.execute(request, currentUser.getId())
      )
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<AppResponse<CategoryResponse>> updateCategory(
    @RequestBody @Valid UpdateCategoryRequest request,
    @PathVariable UUID id,
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    return ResponseEntity.ok(
      AppResponse.success(updateCategoryUseCase.execute(request, id, currentUser.getId()))
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<AppResponse<Void>> deleteCategory(
    @PathVariable UUID id,
    @AuthenticationPrincipal CustomUserDetails currentUser
  ) {
    deleteCategoryUseCase.execute(currentUser.getId(), id);
    return ResponseEntity.ok(
      AppResponse.success(null)
    );
  }
}
