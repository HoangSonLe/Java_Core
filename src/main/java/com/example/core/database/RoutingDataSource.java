package com.example.core.database;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

  @Override
  protected Object determineCurrentLookupKey() {
    DatabaseType dbType = type.get();
    if (dbType == null) {
      return DatabaseType.READ;
    }
    return type.get();
  }

  enum DatabaseType {
    READ,
    WRITE
  }

  private static final ThreadLocal<DatabaseType> type = new ThreadLocal<>();

  public RoutingDataSource(DataSource write, DataSource read) {
    super.setDefaultTargetDataSource(read);
    super.setTargetDataSources(
        Map.of(DatabaseType.WRITE.name(), write, DatabaseType.READ.name(), read));
    super.afterPropertiesSet();
  }

  static void switchToWrite() {
    type.set(DatabaseType.WRITE);
  }

  static void switchToRead() {
    type.set(DatabaseType.READ);
  }

  static void clear() {
    type.remove();
  }
}
