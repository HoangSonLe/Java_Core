package com.example.core.configuration.async;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@AutoConfiguration
@EnableAsync
@EnableConfigurationProperties(AsyncProperties.class)
@ConditionalOnProperty(
    prefix = "core.async",
    name = "enable",
    havingValue = "true",
    matchIfMissing = true)
public class AsyncAutoConfiguration {
  @Bean(name = "customTaskExecutor")
  public ThreadPoolTaskExecutor customTaskExecutor(AsyncProperties asyncProperties) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(asyncProperties.getCorePoolSize());
    executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
    executor.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());
    executor.setQueueCapacity(asyncProperties.getQueueCapacity());
    executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
