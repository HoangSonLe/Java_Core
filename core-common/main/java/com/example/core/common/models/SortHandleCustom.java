package com.example.core.common.models;

import java.util.Locale;
import org.springframework.data.domain.Sort;

/** Utility record for parsing and handling sort parameters. */
public record SortHandleCustom(Sort sort) {

  private static final String ASC = "ascend";
  private static final String DESC = "descend";
  private static final String DEFAULT_FIELD = "createdAt";

  /**
   * Parse sort string in format "field_direction" (e.g. "name_ascend" or "age_descend") into a
   * Spring Data {@link Sort} object.
   *
   * @param sorter sort parameter, example: "createdAt_descend"
   * @return a Sort object, defaults to Sort.by(DESC, "createdAt") if invalid
   */
  public static Sort from(String sorter) {
    if (sorter == null || sorter.isBlank()) {
      return Sort.by(Sort.Direction.DESC, DEFAULT_FIELD);
    }

    String[] parts = sorter.split("_");
    if (parts.length == 2) {
      String field = parts[0];
      String direction = parts[1].toLowerCase(Locale.ROOT);

      return switch (direction) {
        case ASC -> Sort.by(Sort.Direction.ASC, field);
        case DESC -> Sort.by(Sort.Direction.DESC, field);
        default -> Sort.by(Sort.Direction.DESC, DEFAULT_FIELD);
      };
    }

    return Sort.by(Sort.Direction.DESC, DEFAULT_FIELD);
  }
}
