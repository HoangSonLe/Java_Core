package com.example.core.cache;

import com.example.core.common.constants.CoreErrorCodes;
import com.example.core.common.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("redisJwtCache")
public class RedisJwtCache implements IJwtCache {

  @Override
  public void put(String token, Object value, long ttlSeconds) {
    throw new ApplicationException(
        CoreErrorCodes.SERVER_ERROR, "Not implements", HttpStatus.FORBIDDEN);
  }

  @Override
  public Object get(String token) {
    return null;
  }

  @Override
  public void remove(String token) {
    throw new ApplicationException(
        CoreErrorCodes.SERVER_ERROR, "Not implements", HttpStatus.FORBIDDEN);
  }
}
