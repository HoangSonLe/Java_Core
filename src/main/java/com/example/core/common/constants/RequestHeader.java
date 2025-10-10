package com.example.core.common.constants;

public enum RequestHeader {
  PROPERTY_ID("property-id"),
  CLIENT_TIME("client-time"),
  REQUEST_ID("X-Request-Id"),
  IP_ADDRESS("X-Real-IP"),
  IP_FORWARD_FOR("X-Original-Forwarded-For"),
  USER_AGENT("User-Agent"),
  CLIENT_SYSTEM("Client-System"),
  APP_VERSION("App-Version"),
  PLATFORM("Platform"),
  DEVICE_ID("Device-Id"),
  SIGNATURE("Signature"),
  X_ORIGINAL_HOST("X-Original-Host"),
  X_SERVICE("X-Service"),
  X_FORWARDED_FOR("x-forwarded-for"),
  X_ORIGINAL_URI("X-Original-URI"),
  X_ORIGINAL_METHOD("X-Original-METHOD"),
  AUTHORIZATION("Authorization");

  final String value;

  private RequestHeader(final String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
