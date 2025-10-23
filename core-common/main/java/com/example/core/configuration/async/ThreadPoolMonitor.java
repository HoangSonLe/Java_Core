package com.example.core.configuration.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

// Khi async tắt → bean customTaskExecutor không tồn tại → monitor cũng không chạy.
@Slf4j
@Component
@ConditionalOnBean(name = "customTaskExecutor")
public class ThreadPoolMonitor {
  private final ThreadPoolTaskExecutor customTaskExecutor;

  public ThreadPoolMonitor(@Qualifier("customTaskExecutor") ThreadPoolTaskExecutor customTaskExecutor) {
    this.customTaskExecutor = customTaskExecutor;
  }

  @Scheduled(fixedDelay = 10000)
  public void logThreadStats() {
    log.info(
        "[ThreadPool] Active: {}, Pool: {}, Max: {}, Queue size: {}",
        customTaskExecutor.getActiveCount(),
        customTaskExecutor.getPoolSize(),
        customTaskExecutor.getMaxPoolSize(),
        customTaskExecutor.getThreadPoolExecutor().getQueue().size());
  }
}
