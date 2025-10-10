package com.example.core.common.exceptions;

import com.example.core.common.constants.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Getter
public class ApplicationException extends RuntimeException {
  private final transient ErrorCode errorCode;
  final String message;
  final HttpStatus httpStatus;

  public String asMessage() {
    return StringUtils.hasText(this.message) ? this.message : this.errorCode.getMessage();
  }

  public ApplicationException(
      final ErrorCode errorCode, final String message, final HttpStatus httpStatus) {
    this.errorCode = errorCode;
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
