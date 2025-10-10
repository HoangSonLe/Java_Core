package com.example.core.common.exceptions;

import com.example.core.common.constants.CoreErrorCodes;
import org.springframework.http.HttpStatus;

public class CoreException extends ApplicationException {
  public CoreException() {
    super(
        CoreErrorCodes.SYSTEM_ERROR,
        CoreErrorCodes.SYSTEM_ERROR.getMessage(),
        HttpStatus.SERVICE_UNAVAILABLE);
  }
}
