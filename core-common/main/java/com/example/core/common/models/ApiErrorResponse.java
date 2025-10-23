package com.example.core.common.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(
    requiredMode = Schema.RequiredMode.REQUIRED,
    example =
        "{\n \"guid\": \"b05f236e-952c-4bc1-b43a-f484afc2677b\",\n \"code\": \"SYS008\",\n \"message\": \"SYSTEM_AUTHORIZATION\",\n \"path\": \"/api/auth\",\n \"method\": \"POST\",\n \"timestamp\": \"2023-11-08T11:00:58Z\"\n}\n")
public record ApiErrorResponse(
    String guid,
    String code,
    String message,
    String path,
    String method,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") LocalDateTime timestamp) {

  public static ApiErrorResponse ofEmpty() {
    return new ApiErrorResponse("", "", "", "", "", LocalDateTime.MIN);
  }
}
