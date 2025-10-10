package com.example.core.common.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.springframework.lang.NonNull;

/**
 * Generic paginated response object for REST APIs.
 *
 * @param <T> Type of data contained in the page
 */
@Schema(description = "Phản hồi dữ liệu có phân trang")
public record PageResponse<T>(
    @Schema(
            description = "Danh sách dữ liệu trong trang hiện tại",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NonNull
        List<T> data,
    @Schema(
            description = "Trạng thái phản hồi",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
        boolean success,
    @Schema(
            description = "Tổng số phần tử",
            example = "100",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NonNull
        Long total,
    @Schema(
            description = "Số trang hiện tại (bắt đầu từ 1)",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NonNull
        Integer currentPage,
    @Schema(
            description = "Kích thước của mỗi trang",
            example = "20",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NonNull
        Integer pageSize)
    implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Compact constructor with runtime validation. Avoids redundant null checks and SonarQube
   * warnings.
   */
  public PageResponse {
    // Dùng requireNonNull() thay vì Objects.isNull() để tránh cảnh báo "always false"
    Objects.requireNonNull(data, "Data list must not be null");
    Objects.requireNonNull(total, "Total must not be null");
    Objects.requireNonNull(currentPage, "Current page must not be null");
    Objects.requireNonNull(pageSize, "Page size must not be null");

    if (total < 0) {
      throw new IllegalArgumentException("Total must not be negative");
    }
    if (currentPage < 1) {
      throw new IllegalArgumentException("Current page must be >= 1");
    }
    if (pageSize < 1) {
      throw new IllegalArgumentException("Page size must be >= 1");
    }
  }
}
