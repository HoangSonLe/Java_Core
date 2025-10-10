package com.example.core.common.models;

import lombok.NonNull;

public record RequestId(@NonNull String value) {
  public RequestId {
    if (value.isBlank()) {
      throw new IllegalArgumentException("RequestId cannot be blank");
    }
  }

  public static RequestId ofEmpty() {
    return new RequestId("");
  }
}
