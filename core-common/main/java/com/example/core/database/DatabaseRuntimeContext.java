package com.example.core.database;

import java.util.concurrent.atomic.AtomicReference;

public class DatabaseRuntimeContext {
  private static final AtomicReference<DatabaseMode> CURRENT_MODE =
      new AtomicReference<>(DatabaseMode.SYNC);

  private DatabaseRuntimeContext() {}

  public static void setMode(DatabaseMode mode) {
    CURRENT_MODE.set(mode);
  }

  public static DatabaseMode getMode() {
    return CURRENT_MODE.get();
  }

  public static boolean isAsyncMode() {
    return CURRENT_MODE.get() == DatabaseMode.ASYNC;
  }

  public static boolean isSyncMode() {
    return CURRENT_MODE.get() == DatabaseMode.SYNC;
  }
}
