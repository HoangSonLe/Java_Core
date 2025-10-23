package com.core.asyncDatabaseConfig;

import com.example.core.database.DatabaseType;
import reactor.core.publisher.Mono;

public final class DatabaseContextHolder {
  public static final String CONTEXT_KEY = "DB_ROUTING_KEY";

  private DatabaseContextHolder() {}

  public static <T> Mono<T> withWrite(Mono<T> mono) {
    return mono.contextWrite(ctx -> ctx.put(CONTEXT_KEY, DatabaseType.WRITE));
  }

  public static <T> Mono<T> withRead(Mono<T> mono) {
    return mono.contextWrite(ctx -> ctx.put(CONTEXT_KEY, DatabaseType.READ));
  }
}
