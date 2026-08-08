package com.team.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // --- Common & Request Errors ---
  INVALID_INPUT(HttpStatus.BAD_REQUEST, "ERR_REQ_400", "Invalid request parameters"),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ERR_AUTH_401", "Unauthorized access"),
  FORBIDDEN(HttpStatus.FORBIDDEN, "ERR_AUTH_403", "Access denied"),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR_RES_404", "Resource not found"),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "ERR_REQ_405", "Method not supported"),
  RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "ERR_RES_409", "Resource already exists"),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "ERR_RES_400", "Bad request"),

  // --- User Domain Errors ---
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR_USER_001", "User not found"),
  USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "ERR_USER_002", "Email or username already exists"),
  INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "ERR_USER_003", "Invalid username or password"),
  ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "ERR_USER_004", "User account is locked"),

  // --- System Errors ---
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_SYS_500", "Unexpected internal server error");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  ErrorCode(HttpStatus httpStatus, String code, String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
