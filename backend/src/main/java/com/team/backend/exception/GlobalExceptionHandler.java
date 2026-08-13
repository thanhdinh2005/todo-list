package com.team.backend.exception;

import com.team.backend.common.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

  // =========================================================
  // BUSINESS EXCEPTION
  // =========================================================

  @ExceptionHandler(AppException.class)
  public ResponseEntity<AppResponse<Void>> handleAppException(
    AppException ex
  ) {
    ErrorCode code = ex.getErrorCode();

    log.warn(
      "Application error [{}]: {}",
      code.name(),
      code.getMessage()
    );

    return ResponseEntity
      .status(code.getHttpStatus())
      .body(
        AppResponse.error(
          code.getHttpStatus().value(),
          code.getMessage()
        )
      );
  }


  // =========================================================
  // VALIDATION / BAD REQUEST
  // =========================================================

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<AppResponse<Map<String, String>>> handleValidationException(
    MethodArgumentNotValidException ex
  ) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult()
      .getFieldErrors()
      .forEach(error ->
        errors.put(
          error.getField(),
          error.getDefaultMessage()
        )
      );

    log.warn(
      "Validation failed, fields={}",
      errors.keySet()
    );

    return ResponseEntity
      .badRequest()
      .body(
        AppResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          "Validation failed",
          errors
        )
      );
  }


  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<AppResponse<Void>> handleMessageNotReadable(
    HttpMessageNotReadableException ex
  ) {
    log.warn("Malformed or unreadable request body");

    return ResponseEntity
      .badRequest()
      .body(
        AppResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          "Invalid request body"
        )
      );
  }


  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<AppResponse<Void>> handleIllegalArgument(
    IllegalArgumentException ex
  ) {
    log.warn("Illegal argument: {}", ex.getMessage());

    return ResponseEntity
      .badRequest()
      .body(
        AppResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          ex.getMessage()
        )
      );
  }


  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<AppResponse<Void>> handleMissingParameter(
    MissingServletRequestParameterException ex
  ) {
    String message = String.format(
      "Missing required parameter: '%s'",
      ex.getParameterName()
    );

    log.warn("Missing request parameter: {}", ex.getParameterName());

    return ResponseEntity
      .badRequest()
      .body(
        AppResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          message
        )
      );
  }


  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<AppResponse<Void>> handleTypeMismatch(
    MethodArgumentTypeMismatchException ex
  ) {
    String message = String.format(
      "Invalid value for parameter: '%s'",
      ex.getName()
    );

    log.warn(
      "Request parameter type mismatch, parameter={}",
      ex.getName()
    );

    return ResponseEntity
      .badRequest()
      .body(
        AppResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          message
        )
      );
  }


  // =========================================================
  // HTTP METHOD
  // =========================================================

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<AppResponse<Void>> handleMethodNotSupported(
    HttpRequestMethodNotSupportedException ex
  ) {
    String message = String.format(
      "Method '%s' is not supported for this endpoint",
      ex.getMethod()
    );

    log.warn(
      "HTTP method not supported, method={}",
      ex.getMethod()
    );

    return ResponseEntity
      .status(HttpStatus.METHOD_NOT_ALLOWED)
      .body(
        AppResponse.error(
          HttpStatus.METHOD_NOT_ALLOWED.value(),
          message
        )
      );
  }


  // =========================================================
  // DATABASE / DATA INTEGRITY
  // =========================================================

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<AppResponse<Void>> handleDataIntegrityViolation(
    DataIntegrityViolationException ex
  ) {
    log.warn(
      "Data integrity violation: {}",
      ex.getMostSpecificCause().getMessage()
    );

    return ResponseEntity
      .status(HttpStatus.CONFLICT)
      .body(
        AppResponse.error(
          HttpStatus.CONFLICT.value(),
          "Data conflict"
        )
      );
  }


  // =========================================================
  // SECURITY
  // =========================================================

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<AppResponse<Void>> handleBadCredentials(
    BadCredentialsException ex
  ) {
    log.warn("Authentication failed: invalid credentials");

    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(
        AppResponse.error(
          HttpStatus.UNAUTHORIZED.value(),
          "Invalid email or password"
        )
      );
  }


  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<AppResponse<Void>> handleDisabledException(
    DisabledException ex
  ) {
    log.warn("Authentication failed: account disabled");

    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(
        AppResponse.error(
          HttpStatus.UNAUTHORIZED.value(),
          "Your account has been deactivated"
        )
      );
  }


  @ExceptionHandler(LockedException.class)
  public ResponseEntity<AppResponse<Void>> handleLocked(
    LockedException ex
  ) {
    log.warn("Authentication failed: account locked");

    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(
        AppResponse.error(
          HttpStatus.FORBIDDEN.value(),
          "Account locked"
        )
      );
  }


  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<AppResponse<Void>> handleAuthenticationException(
    AuthenticationException ex
  ) {
    log.warn(
      "Authentication failed: {}",
      ex.getClass().getSimpleName()
    );

    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(
        AppResponse.error(
          HttpStatus.UNAUTHORIZED.value(),
          "Not authenticated"
        )
      );
  }


  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<AppResponse<Void>> handleAccessDeniedException(
    AccessDeniedException ex
  ) {
    log.warn("Access denied");

    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(
        AppResponse.error(
          HttpStatus.FORBIDDEN.value(),
          "You are not authorized to perform this request."
        )
      );
  }


  // =========================================================
  // UNEXPECTED EXCEPTION
  // =========================================================

  @ExceptionHandler(Exception.class)
  public ResponseEntity<AppResponse<Void>> handleGenericException(
    Exception ex
  ) {
    String errorId = UUID.randomUUID()
      .toString()
      .substring(0, 8);

    log.error(
      "Unhandled exception [ErrorID: {}]",
      errorId,
      ex
    );

    String message = String.format(
      "The system is experiencing an issue. "
        + "Please contact the administrator and provide "
        + "the error code: %s",
      errorId
    );

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(
        AppResponse.error(
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          message
        )
      );
  }
}
