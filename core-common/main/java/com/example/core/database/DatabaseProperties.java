package com.example.core.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "database")
public class DatabaseProperties {
  private DataSourceProperties writer;
  private DataSourceProperties reader;

  @Getter
  @Setter
  public static class DataSourceProperties {
    private String url;
    private String username;
    private String password;
  }
}
