package com.example.core.common.exceptions;

import com.example.core.common.constants.CoreErrorCodes;
import org.springframework.http.HttpStatus;

public class ConflictsException extends ApplicationException {
  public ConflictsException() {
    super(CoreErrorCodes.CONFLICT, CoreErrorCodes.CONFLICT.getMessage(), HttpStatus.CONFLICT);
  }
}
