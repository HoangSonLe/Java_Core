package com.example.core.utils;

import com.example.core.common.annotations.SearchableField;
import java.lang.reflect.Field;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class SearchFieldUtils {
  private SearchFieldUtils() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  public static String buildString(Object entity) {
    if (entity == null) {
      return "";
    }
    var fields =
        Arrays.stream(entity.getClass().getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(SearchableField.class))
            .toList();

    String combined =
        fields.stream()
            .map(field -> getFieldValueAsString(field, entity))
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.joining("; "));

    return removeDiacritics(combined).toLowerCase();
  }

  private static String getFieldValueAsString(Field field, Object entity) {
    field.setAccessible(true);
    try {
      Object value = field.get(entity);
      if (value == null) return null;
      SearchableField ann = field.getAnnotation(SearchableField.class);
      return (ann.name().isEmpty()) ? value.toString() : ann.name() + "=" + value;
    } catch (IllegalAccessException e) {
      return null;
    }
  }

  public static String removeDiacritics(String input) {
    if (input == null) return "";
    return Normalizer.normalize(input, Normalizer.Form.NFD)
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // regex: cần replaceAll
        .replace("đ", "d") // literal: dùng replace
        .replace("Đ", "D"); // literal: dùng replace
  }
}
