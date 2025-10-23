package com.example.core.security;

import com.example.core.common.constants.CoreErrorCodes;
import com.example.core.common.constants.RequestHeader;
import com.example.core.common.models.ApiErrorResponse;
import com.example.core.common.models.RequestId;
import com.example.core.utils.JsonHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class CustomAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

  public RequestId requestUID(HttpServletRequest request) {
    String headerRequestId = request.getHeader(RequestHeader.REQUEST_ID.getValue());
    return Objects.nonNull(headerRequestId)
        ? new RequestId(headerRequestId)
        : new RequestId(UUID.randomUUID().toString());
  }

  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
      throws IOException {
    response.addHeader("WWW-Authenticate", "");
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    PrintWriter writer = response.getWriter();
    String errorMsg = CoreErrorCodes.SYSTEM_AUTHORIZATION.getMessage();
    if (authEx instanceof OAuth2AuthenticationException oauth2Exception) {
      OAuth2Error error = oauth2Exception.getError();
      String authorization = request.getHeader("Authorization");
      authorization = Objects.isNull(authorization) ? "" : authorization;
      String var10000 = error.getDescription();
      errorMsg = var10000 + " " + authorization;
    }

    ApiErrorResponse apiResponse =
        new ApiErrorResponse(
            this.requestUID(request).value(),
            CoreErrorCodes.SYSTEM_AUTHORIZATION.getCode(),
            errorMsg,
            request.getRequestURI(),
            request.getMethod(),
            LocalDateTime.now());
    response.setHeader(
        "X-SERVICE", Objects.isNull(SystemUtils.getHostName()) ? "DEV" : SystemUtils.getHostName());
    writer.println(JsonHelper.toJson(apiResponse));
  }

  @Override
  public void afterPropertiesSet() {
    this.setRealmName("PACKAGE_FROM_SON");
    super.afterPropertiesSet();
  }
}
