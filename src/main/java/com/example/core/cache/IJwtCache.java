package com.example.core.cache;

public interface IJwtCache {
  void put(String token, Object value, long ttlSeconds);

  Object get(String token);

  void remove(String token);
}
