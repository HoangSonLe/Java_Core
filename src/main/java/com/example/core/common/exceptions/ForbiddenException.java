package com.example.core.common.exceptions;

import com.example.core.common.constants.CoreErrorCodes;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApplicationException {
  public ForbiddenException() {
    super(CoreErrorCodes.FORBIDDEN, CoreErrorCodes.FORBIDDEN.getMessage(), HttpStatus.FORBIDDEN);
  }
}
