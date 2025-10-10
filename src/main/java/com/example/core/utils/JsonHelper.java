package com.example.core.utils;

import com.example.core.common.constants.CoreErrorCodes;
import com.example.core.common.exceptions.ApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

public final class JsonHelper {
  private static final ObjectMapper mapper = new ObjectMapper();

  private JsonHelper() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  public static String toJson(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      return "";
    }
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    try {
      return mapper.readValue(json, clazz);
    } catch (Exception e) {
      throw new ApplicationException(
          CoreErrorCodes.CONFLICT, "Error parse JSON", HttpStatus.BAD_REQUEST);
    }
  }
}
