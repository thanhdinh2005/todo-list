package com.team.backend.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppResponse<T> {
  private final LocalDateTime timestamp;
  private final int status;
  private final String code;
  private final String message;
  private final T data;

  public AppResponse(LocalDateTime timestamp, int status, String code, String message, T data) {
    this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    this.status = status;
    this.code = code;
    this.message = message;
    this.data = data;
  }

  // --- Success Factory Methods ---
  public static <T> AppResponse<T> success(T data) {
    return AppResponse.<T>builder()
      .timestamp(LocalDateTime.now())
      .status(200)
      .code("SUCCESS")
      .message("Success")
      .data(data)
      .build();
  }

  public static <T> AppResponse<T> success(String message, T data) {
    return AppResponse.<T>builder()
      .timestamp(LocalDateTime.now())
      .status(200)
      .code("SUCCESS")
      .message(message)
      .data(data)
      .build();
  }

  // --- Error Factory Methods ---
  public static <T> AppResponse<T> error(int status, String code, String message) {
    return AppResponse.<T>builder()
      .timestamp(LocalDateTime.now())
      .status(status)
      .code(code)
      .message(message)
      .build();
  }

  public static <T> AppResponse<T> error(int status, String code, String message, T data) {
    return AppResponse.<T>builder()
      .timestamp(LocalDateTime.now())
      .status(status)
      .code(code)
      .message(message)
      .data(data)
      .build();
  }
}
