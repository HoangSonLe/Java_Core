package com.example.core.advice;

import com.example.core.common.constants.CoreErrorCodes;
import com.example.core.common.constants.RequestHeader;
import com.example.core.common.exceptions.ApplicationException;
import com.example.core.common.exceptions.ResourceNotFoundException;
import com.example.core.common.models.ApiErrorResponse;
import com.example.core.common.models.RequestId;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  public RequestId requestUID(HttpServletRequest request) {
    String headerRequestId = request.getHeader(RequestHeader.REQUEST_ID.getValue());
    return Objects.nonNull(headerRequestId)
        ? new RequestId(headerRequestId)
        : new RequestId(UUID.randomUUID().toString());
  }

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ApiErrorResponse> handleApplicationException(
      final ApplicationException exception, final HttpServletRequest request) {
    ApiErrorResponse response =
        new ApiErrorResponse(
            this.requestUID(request).value(),
            exception.getErrorCode().toString(),
            exception.getMessage(),
            request.getRequestURI(),
            request.getMethod(),
            LocalDateTime.now());
    return new ResponseEntity<>(response, exception.getHttpStatus());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<String> handleResponseStatus(ResponseStatusException ex) {
    return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
  }

  @ExceptionHandler({
    HttpRequestMethodNotSupportedException.class,
    HttpMediaTypeNotSupportedException.class,
    HttpMediaTypeNotAcceptableException.class,
    MissingPathVariableException.class,
    MissingServletRequestParameterException.class,
    MissingServletRequestPartException.class,
    ServletRequestBindingException.class,
    MethodArgumentNotValidException.class,
    NoHandlerFoundException.class,
    AsyncRequestTimeoutException.class,
    ErrorResponseException.class,
    ConversionNotSupportedException.class,
    TypeMismatchException.class,
    HttpMessageNotReadableException.class,
    HttpMessageNotWritableException.class,
    BindException.class
  })
  public ResponseEntity<ApiErrorResponse> handleBindException(
      final BindException exception, final HttpServletRequest request) {
    FieldError fieldError =
        exception.getFieldErrors().stream().findFirst().orElse(new FieldError("", "", ""));
    ApiErrorResponse response =
        new ApiErrorResponse(
            this.requestUID(request).value(),
            CoreErrorCodes.BAD_REQUEST.getCode(),
            fieldError.getDefaultMessage(),
            request.getRequestURI(),
            request.getMethod(),
            LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiErrorResponse> handleBindException(
      final RuntimeException exception, final HttpServletRequest request) {
    ApiErrorResponse response =
        new ApiErrorResponse(
            this.requestUID(request).value(),
            CoreErrorCodes.BAD_REQUEST.getCode(),
            exception.getCause().getMessage(),
            request.getRequestURI(),
            request.getMethod(),
            LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleException(
      final Exception exception, final HttpServletRequest request) {
    ApiErrorResponse response =
        new ApiErrorResponse(
            this.requestUID(request).value(),
            CoreErrorCodes.SYSTEM_ERROR.getCode(),
            CoreErrorCodes.SYSTEM_ERROR.getMessage(),
            request.getRequestURI(),
            request.getMethod(),
            LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
