package com.example.core.configuration.async;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "core.async")
public class AsyncProperties {
  private boolean enabled = false;
  private int corePoolSize = 4;
  private int maxPoolSize = 8;
  private int keepAliveSeconds = 60;
  private int queueCapacity = 100;
  private String threadNamePrefix = "core-async-";
}
