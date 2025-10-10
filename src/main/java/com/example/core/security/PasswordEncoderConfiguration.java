package com.example.core.security;

import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class PasswordEncoderConfiguration {

  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
}
