package com.example.core.common.models;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public record PageRequestCustom(PageRequest pageRequest) {
  static int MAX_PAGE_SIZE = 500;

  public int currentPage() {
    return pageRequest.getPageNumber() + 1;
  }

  public static PageRequestCustom of(int page, int size, Sort sort) {
    if (size > MAX_PAGE_SIZE) {
      size = MAX_PAGE_SIZE;
    }
    return new PageRequestCustom(PageRequest.of(page - 1, size, sort));
  }

  public static PageRequestCustom of(int page, int size) {
    if (size > MAX_PAGE_SIZE) {
      size = MAX_PAGE_SIZE;
    }
    return new PageRequestCustom(PageRequest.of(page - 1, size));
  }
}
