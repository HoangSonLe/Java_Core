package com.example.core.common.exceptions;

import com.example.core.common.constants.CoreErrorCodes;
import org.springframework.http.HttpStatus;

public class UnavailableException extends ApplicationException {
  public UnavailableException() {
    super(
        CoreErrorCodes.SERVICE_UNAVAILABLE,
        CoreErrorCodes.SERVICE_UNAVAILABLE.getMessage(),
        HttpStatus.SERVICE_UNAVAILABLE);
  }
}
