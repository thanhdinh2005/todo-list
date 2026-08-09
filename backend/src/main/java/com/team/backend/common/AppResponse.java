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
  private final String message;
  private final T data;

  public AppResponse(LocalDateTime timestamp, int status, String message, T data) {
    this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    this.status = status;
    this.message = message;
    this.data = data;
  }

  public static <T> AppResponse<T> error(int status, String message) {
    return AppResponse.<T>builder()
      .timestamp(LocalDateTime.now())
      .status(status)
      .message(message)
      .build();
  }

  public static <T> AppResponse<T> error(int status, String message, T data) {
    return AppResponse.<T>builder()
      .timestamp(LocalDateTime.now())
      .status(status)
      .message(message)
      .data(data)
      .build();
  }

  public static <T> AppResponse<T> success(T data) {
    return AppResponse.<T>builder()
      .timestamp(LocalDateTime.now())
      .status(200)
      .message("Success")
      .data(data)
      .build();
  }
}
