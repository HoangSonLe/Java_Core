package com.example.core.database;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "core.database")
public class DatabaseModeConfig {

  private String mode = "sync"; // default

  @PostConstruct
  public void init() {
    DatabaseRuntimeContext.setMode(
        "async".equalsIgnoreCase(mode) ? DatabaseMode.ASYNC : DatabaseMode.SYNC);
    log.info("⚙️  Database mode initialized: " + DatabaseRuntimeContext.getMode());
  }
}
