package com.team.backend.apispec;

import com.team.backend.common.AppResponse;
import com.team.backend.dto.request.task.CreateTaskRequest;
import com.team.backend.dto.request.task.TaskFilterParam;
import com.team.backend.dto.request.task.UpdateTaskRequest;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.dto.response.TaskResponse;
import com.team.backend.dto.response.TaskStatsResponse;
import com.team.backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Task", description = "Quản lý task của user")
@SecurityRequirement(name = "bearerAuth")
public interface TaskApiSpec {

  @Operation(
    summary = "Danh sách task quá hạn",
    description = "Task chưa hoàn thành và đã qua dueDate. **Lưu ý:** response KHÔNG wrap trong AppResponse, trả thẳng PageResponse."
  )
  ResponseEntity<PageResponse<TaskResponse>> getAllOverdueTasks(
    TaskFilterParam filter, CustomUserDetails currentUser
  );

  @Operation(
    summary = "Thống kê nhanh",
    description = "Tổng số task, số hoàn thành, số pending, số quá hạn của user hiện tại"
  )
  @ApiResponse(responseCode = "200", description = "Thành công")
  ResponseEntity<AppResponse<TaskStatsResponse>> taskStats(CustomUserDetails currentUser);

  @Operation(
    summary = "Danh sách task của tôi",
    description = "Filter theo completed, categoryId, dueBefore, dueAfter, keyword; có phân trang. " +
      "**Lưu ý:** response KHÔNG wrap trong AppResponse, trả thẳng PageResponse."
  )
  ResponseEntity<PageResponse<TaskResponse>> getAllTasks(
    TaskFilterParam filter, CustomUserDetails currentUser
  );

  @Operation(summary = "Tạo task mới")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Tạo thành công"),
    @ApiResponse(responseCode = "404", description = "Category không tồn tại"),
    @ApiResponse(responseCode = "403", description = "Category không thuộc về user hiện tại")
  })
  ResponseEntity<AppResponse<TaskResponse>> createTask(
    CustomUserDetails currentUser, CreateTaskRequest request
  );

  @Operation(
    summary = "Đánh dấu hoàn thành",
    description = "Set completed = true cho task"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Thành công"),
    @ApiResponse(responseCode = "403", description = "Không có quyền"),
    @ApiResponse(responseCode = "404", description = "Task không tồn tại")
  })
  ResponseEntity<AppResponse<TaskResponse>> markAsCompleteTask(
    CustomUserDetails currentUser,
    @Parameter(description = "ID của task") UUID taskId
  );

  @Operation(
    summary = "Cập nhật task",
    description = "Chỉ update field được gửi trong request; field không gửi giữ nguyên giá trị cũ"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
    @ApiResponse(responseCode = "403", description = "Không có quyền sửa task này"),
    @ApiResponse(responseCode = "404", description = "Task hoặc category không tồn tại")
  })
  ResponseEntity<AppResponse<TaskResponse>> updateTaskById(
    CustomUserDetails currentUser,
    UpdateTaskRequest request,
    @Parameter(description = "ID của task cần cập nhật") UUID taskId
  );

  @Operation(
    summary = "Chi tiết 1 task",
    description = "Chính chủ hoặc user có quyền task:read_any"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Thành công"),
    @ApiResponse(responseCode = "403", description = "Không có quyền xem task này"),
    @ApiResponse(responseCode = "404", description = "Task không tồn tại")
  })
  ResponseEntity<AppResponse<TaskResponse>> getTaskById(
    @Parameter(description = "ID của task") UUID taskId,
    CustomUserDetails currentUser
  );
}
