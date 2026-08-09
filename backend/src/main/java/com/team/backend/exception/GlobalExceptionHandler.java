package com.team.backend.exception;

import com.team.backend.common.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // 1. Bắt các lỗi nghiệp vụ chủ động ném ra từ Service
  @ExceptionHandler(AppException.class)
  public ResponseEntity<AppResponse<Void>> handleAppException(AppException ex) {
    ErrorCode code = ex.getErrorCode();
    log.warn("App error [{}]: {}", code.name(), code.getMessage());
    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(code.getHttpStatus().value(), code.getMessage()));
  }

  // 2. Bắt lỗi tham số không hợp lệ
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<AppResponse<Void>> illegalArgumentExceptionHandler(IllegalArgumentException e) {
    log.warn("Illegal argument: {}", e.getMessage());
    return ResponseEntity
      .badRequest()
      .body(AppResponse.error(400, e.getMessage()));
  }

  // 3. Bắt lỗi Validation (Khi dùng @Valid ở DTO)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<AppResponse<Map<String, String>>> handleValidationException(
    MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      errors.put(error.getField(), error.getDefaultMessage());
    });

    log.warn("Validation failed: {}", errors);
    return ResponseEntity
      .badRequest()
      .body(AppResponse.error(400, "Validation failed", errors));
  }

  // 4. Bắt lỗi sai kiểu dữ liệu param trên URL
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<AppResponse<Void>> handleTypeMismatch(
    MethodArgumentTypeMismatchException ex) {

    String message = String.format("Invalid parameter '%s': '%s'", ex.getName(), ex.getValue());
    log.warn("Type mismatch: {}", message);
    return ResponseEntity
      .badRequest()
      .body(AppResponse.error(400, message));
  }

  // 5. Bắt lỗi thiếu param bắt buộc trên URL
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<AppResponse<Void>> handleMissingParam(
    MissingServletRequestParameterException ex) {

    String message = String.format("Missing required parameter: '%s'", ex.getParameterName());
    log.warn("Missing param: {}", message);
    return ResponseEntity
      .badRequest()
      .body(AppResponse.error(400, message));
  }

  // 6. Bắt lỗi sai HTTP Method
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<AppResponse<Void>> handleMethodNotSupported(
    HttpRequestMethodNotSupportedException ex) {

    String message = String.format("Method '%s' is not supported for this endpoint, use: %s",
      ex.getMethod(),
      ex.getSupportedMethods() != null ? String.join(", ", ex.getSupportedMethods()) : "unknown");

    log.warn("Method not supported: {}", message);
    return ResponseEntity
      .status(HttpStatus.METHOD_NOT_ALLOWED)
      .body(AppResponse.error(405, message));
  }

  /* =========================================================
   * NHÓM LỖI SECURITY (Bắt ở tầng Controller - ví dụ: @PreAuthorize)
   * ========================================================= */

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<AppResponse<Void>> handleDisabledException(DisabledException ex) {
    log.warn("User disabled login attempt: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(AppResponse.error(401, "Your account has been deactivated"));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<AppResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
    log.warn("Bad credentials login attempt: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(AppResponse.error(401, "Invalid email or password"));
  }

  @ExceptionHandler(LockedException.class)
  public ResponseEntity<AppResponse<Void>> handleLocked(LockedException ex) {
    log.warn("Locked account login attempt: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(AppResponse.error(403, "Account locked"));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<AppResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
    log.warn("Authentication error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(AppResponse.error(401, "Not authenticated"));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<AppResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
    log.warn("Access denied error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(AppResponse.error(403, "You are not authorized to perform this request."));
  }

  /* =========================================================
   * NHÓM LỖI HỆ THỐNG MẶC ĐỊNH (Catch-all 500)
   * ========================================================= */

  @ExceptionHandler(Exception.class)
  public ResponseEntity<AppResponse<Void>> handleGenericException(Exception ex) {
    String traceId = UUID.randomUUID().toString().substring(0, 8);

    // Ghi log ở mức ERROR (sẽ đẩy ra file error.log), đính kèm traceId và stacktrace đầy đủ
    log.error("Unhandled exception [TraceID: {}]: ", traceId, ex);

    // Trả message an toàn cho Client kèm theo TraceID để tiện tra cứu
    String userMessage = String.format("The system is experiencing an issue. Please contact the administrator and provide the error code: %s", traceId);

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(AppResponse.error(500, userMessage));
  }
}
