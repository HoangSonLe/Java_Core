package com.example.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component("inMemoryJwtCache")
public class InMemoryJwtCache implements IJwtCache {
  private final Cache<String, CacheValue> cache;

  private record CacheValue(Object value, long ttlSeconds) {}

  public InMemoryJwtCache() {
    this.cache =
        Caffeine.newBuilder()
            //                .expireAfterWrite(1, TimeUnit.HOURS)
            .expireAfter(
                new Expiry<String, CacheValue>() {
                  @Override
                  public long expireAfterCreate(String key, CacheValue value, long currentTime) {
                    // TTL theo từng entry (nanoseconds)
                    return TimeUnit.SECONDS.toNanos(value.ttlSeconds);
                  }

                  @Override
                  public long expireAfterUpdate(
                      String key, CacheValue value, long currentTime, long currentDuration) {
                    return currentDuration; // giữ nguyên TTL cũ
                  }

                  @Override
                  public long expireAfterRead(
                      String key, CacheValue value, long currentTime, long currentDuration) {
                    return currentDuration; // không thay đổi TTL khi đọc
                  }
                })
            .maximumSize(10000)
            .build();
  }

  @Override
  public void put(String token, Object value, long ttlSeconds) {
    cache.put(token, new CacheValue(value, ttlSeconds));
  }

  @Override
  public Object get(String token) {
    CacheValue cacheValue = cache.getIfPresent(token);
    return cacheValue != null ? cacheValue.value() : null;
  }

  @Override
  public void remove(String token) {
    cache.invalidate(token);
  }
}
