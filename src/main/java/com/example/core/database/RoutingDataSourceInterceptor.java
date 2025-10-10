package com.example.core.database;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aspect chặn (intercept) các phương thức hoặc lớp có gắn @Transactional để tự động định tuyến
 * (route) kết nối Database: - Nếu @Transactional(readOnly = true) -> dùng READONLY -
 * Nếu @Transactional(readOnly = false) -> dùng WRITE
 */
@Aspect
@Order(0)
public class RoutingDataSourceInterceptor {

  /** Thực thi logic chuyển hướng DataSource */
  private Object proceed(ProceedingJoinPoint joinPoint, Transactional transactional)
      throws Throwable {

    try {
      // Luôn ưu tiên read nếu có @Transactional (dù bạn không ghi readOnly=true)
      if (transactional == null || transactional.readOnly()) {
        // Chỉ đọc -> dùng reader
        RoutingDataSource.switchToRead();
      } else {
        // Có ghi -> dùng writer
        RoutingDataSource.switchToWrite();
      }

      // Gọi method gốc
      return joinPoint.proceed();
    } finally {
      // Luôn dọn dẹp context để tránh rò rỉ giữa các request
      RoutingDataSource.clear();
    }
  }

  /** Chặn tất cả phương thức nằm trong class có gắn @Transactional */
  @Around("@within(transactional)")
  public Object proceedByClassAnnotation(ProceedingJoinPoint joinPoint, Transactional transactional)
      throws Throwable {
    return proceed(joinPoint, transactional);
  }

  /** Chặn những method được gắn trực tiếp @Transactional */
  @Around("@annotation(transactional)")
  public Object proceedByMethodAnnotation(
      ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
    return proceed(joinPoint, transactional);
  }
}
