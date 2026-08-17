package com.team.backend.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

  private final ErrorCode errorCode;
  private String customeMessage;

  public AppException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.customeMessage = errorCode.getMessage();
  }

  public AppException(ErrorCode errorCode, String customMessage) {
    super(customMessage);
    this.customeMessage = customMessage;
    this.errorCode = errorCode;
  }
}
