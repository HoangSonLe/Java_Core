package com.example.core.common.exceptions;

import com.example.core.common.constants.CoreErrorCodes;
import org.springframework.http.HttpStatus;

public class TokenRefreshException extends ApplicationException {
  public TokenRefreshException() {
    super(
        CoreErrorCodes.TOKEN_INVALID,
        CoreErrorCodes.TOKEN_INVALID.getMessage(),
        HttpStatus.UNAUTHORIZED);
  }
}
