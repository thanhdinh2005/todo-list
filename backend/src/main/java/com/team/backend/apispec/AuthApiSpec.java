package com.team.backend.apispec;

import com.team.backend.common.AppResponse;
import com.team.backend.dto.request.auth.LoginRequest;
import com.team.backend.dto.request.auth.LogoutRequest;
import com.team.backend.dto.request.auth.RefreshTokenRequest;
import com.team.backend.dto.request.auth.RegisterRequest;
import com.team.backend.dto.response.LoginResponse;
import com.team.backend.dto.response.RegisterResponse;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "Đăng ký, đăng nhập, quản lý phiên đăng nhập")
public interface AuthApiSpec {

  @Operation(
    summary = "Đăng ký tài khoản mới",
    description = "Tạo user mới với email/password. Không yêu cầu đăng nhập."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Đăng ký thành công"),
    @ApiResponse(responseCode = "409", description = "Email đã tồn tại")
  })
  ResponseEntity<AppResponse<RegisterResponse>> register(RegisterRequest request);

  @Operation(
    summary = "Đăng nhập",
    description = "Trả về access token và refresh token"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
    @ApiResponse(responseCode = "401", description = "Email hoặc mật khẩu không đúng")
  })
  ResponseEntity<AppResponse<LoginResponse>> login(LoginRequest request);

  @Operation(
    summary = "Làm mới access token",
    description = "Dùng refresh token còn hạn để lấy access token mới"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Làm mới thành công"),
    @ApiResponse(responseCode = "401", description = "Refresh token không hợp lệ hoặc đã hết hạn")
  })
  ResponseEntity<AppResponse<LoginResponse>> refresh(RefreshTokenRequest request);

  @Operation(
    summary = "Đăng xuất",
    description = "Vô hiệu hóa refresh token hiện tại"
  )
  @ApiResponse(responseCode = "200", description = "Đăng xuất thành công")
  ResponseEntity<AppResponse<Void>> logout(LogoutRequest request);

  @Operation(
    summary = "Thông tin tài khoản hiện tại",
    description = "Yêu cầu đã đăng nhập (Bearer token)"
  )
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Thành công"),
    @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ")
  })
  ResponseEntity<AppResponse<UserResponse>> getMe(CustomUserDetails currentUser);
}
