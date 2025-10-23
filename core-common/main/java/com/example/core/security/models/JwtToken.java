package com.example.core.security.models;

import lombok.Builder;

@Builder
public record JwtToken(
    String accessToken,
    String refreshToken,
    String tokenType,
    String userId,
    String userName,
    String email) {
  public JwtToken {
    if (tokenType == null || tokenType.isBlank()) {
      tokenType = "Bearer";
    }
  }
}
