package com.example.core.common.exceptions;

import com.example.core.common.constants.CoreErrorCodes;
import org.springframework.http.HttpStatus;

public class RequestInvalidException extends ApplicationException {
  public RequestInvalidException() {
    super(CoreErrorCodes.REQUEST_INVALID, "RequestInvalidException", HttpStatus.BAD_REQUEST);
  }
}
