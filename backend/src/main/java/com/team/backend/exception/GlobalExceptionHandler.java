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

  // 1. Bắt các lỗi nghiệp vụ chủ động ném ra từ App (AppException)
  @ExceptionHandler(AppException.class)
  public ResponseEntity<AppResponse<Void>> handleAppException(AppException ex) {
    ErrorCode code = ex.getErrorCode();
    log.warn("App error [{}]: {}", code.getCode(), ex.getMessage());

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        ex.getMessage() // Ưu tiên ex.getMessage() để lấy custom dynamic message
      ));
  }

  // 2. Bắt lỗi IllegalArgumentException (chuyển về ErrorCode.INVALID_INPUT)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<AppResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("Illegal argument: {}", ex.getMessage());
    ErrorCode code = ErrorCode.INVALID_INPUT;

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        ex.getMessage()
      ));
  }

  // 3. Bắt lỗi Validation (Khi dùng @Valid ở DTO)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<AppResponse<Map<String, String>>> handleValidationException(
    MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
      errors.put(error.getField(), error.getDefaultMessage())
    );

    log.warn("Validation failed: {}", errors);
    ErrorCode code = ErrorCode.INVALID_INPUT;

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        "Validation failed",
        errors
      ));
  }

  // 4. Fallback: Bắt tất cả các lỗi không xác định khác (Ngoại lệ hệ thống 500)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<AppResponse<Void>> handleGeneralException(Exception ex) {
    log.error("Unhandled exception: ", ex);
    ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        code.getMessage()
      ));
  }

  // 5. Bắt lỗi thiếu param bắt buộc trên URL
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<AppResponse<Void>> handleMissingParam(
    MissingServletRequestParameterException ex) {

    ErrorCode code = ErrorCode.INVALID_INPUT;
    String message = String.format("Missing required parameter: '%s'", ex.getParameterName());
    log.warn("Missing param: {}", message);

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        message
      ));
  }

  // 6. Bắt lỗi sai HTTP Method
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<AppResponse<Void>> handleMethodNotSupported(
    HttpRequestMethodNotSupportedException ex) {

    ErrorCode code = ErrorCode.METHOD_NOT_ALLOWED;
    String message = String.format("Method '%s' is not supported for this endpoint, use: %s",
      ex.getMethod(),
      ex.getSupportedMethods() != null ? String.join(", ", ex.getSupportedMethods()) : "unknown");

    log.warn("Method not supported: {}", message);

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        message
      ));
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<AppResponse<Void>> handleDisabledException(DisabledException ex) {
    ErrorCode code = ErrorCode.ACCOUNT_LOCKED;
    log.warn("User disabled login attempt: {}", ex.getMessage());

    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(AppResponse.error(
        HttpStatus.UNAUTHORIZED.value(),
        code.getCode(),
        "Your account has been deactivated"
      ));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<AppResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
    ErrorCode code = ErrorCode.INVALID_CREDENTIALS;
    log.warn("Bad credentials login attempt: {}", ex.getMessage());

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        "Invalid email or password"
      ));
  }

  @ExceptionHandler(LockedException.class)
  public ResponseEntity<AppResponse<Void>> handleLocked(LockedException ex) {
    ErrorCode code = ErrorCode.ACCOUNT_LOCKED;
    log.warn("Locked account login attempt: {}", ex.getMessage());

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        "Account locked"
      ));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<AppResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
    ErrorCode code = ErrorCode.UNAUTHORIZED;
    log.warn("Authentication error: {}", ex.getMessage());

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        "Not authenticated"
      ));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<AppResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
    ErrorCode code = ErrorCode.FORBIDDEN;
    log.warn("Access denied error: {}", ex.getMessage());

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        "You are not authorized to perform this request."
      ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<AppResponse<Void>> handleGenericException(Exception ex) {
    ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;
    String traceId = UUID.randomUUID().toString().substring(0, 8);

    log.error("Unhandled exception [TraceID: {}]: ", traceId, ex);

    String userMessage = String.format(
      "The system is experiencing an issue. Please contact support and provide error trace ID: %s",
      traceId
    );

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(AppResponse.error(
        code.getHttpStatus().value(),
        code.getCode(),
        userMessage
      ));
  }
}
