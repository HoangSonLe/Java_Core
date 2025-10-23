package com.example.core.common.exceptions;

import com.example.core.common.constants.CoreErrorCodes;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApplicationException {
  public UnauthorizedException() {
    super(
        CoreErrorCodes.SYSTEM_AUTHORIZATION,
        CoreErrorCodes.SYSTEM_AUTHORIZATION.getMessage(),
        HttpStatus.UNAUTHORIZED);
  }
}
