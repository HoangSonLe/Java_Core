package com.example.core.security;

import com.example.core.security.models.JwtProperties;
import com.example.core.security.models.SecurityProperties;
import com.example.core.security.services.JWTTokenService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * Auto-configuration for core security module.
 * This will be automatically loaded when the module is included in a project.
 * It registers all security-related beans including JWT services and security configurations.
 */
@AutoConfiguration
@ConditionalOnClass(JwtDecoder.class)
@EnableConfigurationProperties({JwtProperties.class, SecurityProperties.class})
@ComponentScan(basePackages = "com.example.core")
@Import({
    JWTTokenService.class,
    SecurityConfiguration.class
})
public class SecurityAutoConfiguration {
  // Beans are automatically registered via @ComponentScan
  // No need to manually create beans for @Component classes
}
