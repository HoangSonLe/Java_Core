package com.example.core.common.exceptions;

import com.example.core.common.constants.CoreErrorCodes;
import org.springframework.http.HttpStatus;

public class ServerErrorException extends ApplicationException {
  public ServerErrorException() {
    super(
        CoreErrorCodes.SERVER_ERROR,
        CoreErrorCodes.SERVER_ERROR.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
