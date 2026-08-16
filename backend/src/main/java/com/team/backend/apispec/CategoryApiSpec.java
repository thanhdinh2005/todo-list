package com.team.backend.apispec;

import com.team.backend.common.AppResponse;
import com.team.backend.dto.request.category.CreateCategoryRequest;
import com.team.backend.dto.request.category.UpdateCategoryRequest;
import com.team.backend.dto.response.CategoryResponse;
import com.team.backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Category", description = "Quản lý category của user")
@SecurityRequirement(name = "bearerAuth")
public interface CategoryApiSpec {

  @Operation(
    summary = "Danh sách category của tôi",
    description = "Trả về toàn bộ category thuộc về user hiện tại, không phân trang"
  )
  @ApiResponse(responseCode = "200", description = "Thành công")
  ResponseEntity<AppResponse<List<CategoryResponse>>> getAllCategories(CustomUserDetails currentUser);

  @Operation(
    summary = "Tạo category mới",
    description = "Tên category không được trùng với category khác của cùng user"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Tạo thành công"),
    @ApiResponse(responseCode = "409", description = "Tên category đã tồn tại")
  })
  ResponseEntity<AppResponse<CategoryResponse>> createCategory(
    CreateCategoryRequest request, CustomUserDetails currentUser
  );

  @Operation(
    summary = "Cập nhật category",
    description = "Chỉ chính chủ mới được sửa"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
    @ApiResponse(responseCode = "403", description = "Không có quyền sửa category này"),
    @ApiResponse(responseCode = "404", description = "Category không tồn tại")
  })
  ResponseEntity<AppResponse<CategoryResponse>> updateCategory(
    UpdateCategoryRequest request,
    @Parameter(description = "ID của category cần cập nhật") UUID id,
    CustomUserDetails currentUser
  );

  @Operation(
    summary = "Xóa category",
    description = "Chỉ chính chủ mới được xóa. Task đang gán category này sẽ bị gỡ category (set null), không bị xóa theo."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Xóa thành công"),
    @ApiResponse(responseCode = "403", description = "Không có quyền xóa category này"),
    @ApiResponse(responseCode = "404", description = "Category không tồn tại")
  })
  ResponseEntity<AppResponse<Void>> deleteCategory(
    @Parameter(description = "ID của category cần xóa") UUID id,
    CustomUserDetails currentUser
  );
}
