package com.example.core.utils;

public final class StringHelper {

  private StringHelper() {
    // ngăn không cho new StringHelper()
  }

  public static boolean isNullOrEmpty(String str) {
    return str == null || str.isEmpty();
  }

  public static boolean isNullOrBlank(String str) {
    return str == null || str.isBlank();
  }

  public static boolean hasText(String str) {
    return str != null && !str.isBlank() && !str.isEmpty();
  }
}
