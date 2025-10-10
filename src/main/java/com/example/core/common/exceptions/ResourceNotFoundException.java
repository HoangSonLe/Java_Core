package com.example.core.common.exceptions;

import com.example.core.common.constants.CoreErrorCodes;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApplicationException {
  public ResourceNotFoundException() {
    super(
        CoreErrorCodes.RESOURCE_NOT_FOUND,
        CoreErrorCodes.RESOURCE_NOT_FOUND.getMessage(),
        HttpStatus.NOT_FOUND);
  }
}
