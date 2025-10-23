package com.example.core.security;

import com.example.core.security.models.SecurityProperties;
import java.util.List;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

  @NonNull CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @NonNull CustomAccessDeniedHandler customAccessDeniedHandler;

  @NonNull SecurityProperties securityProperties;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    List<String> permitAll = securityProperties.getPermitAll();
    permitAll.add("/swagger-ui/**");
    permitAll.add("/api-docs.yaml");
    permitAll.add("/api-docs/**");
    permitAll.add("/x/api-docs.yaml");
    permitAll.add("/x/api-docs/**");
    permitAll.add("/error/**");
    permitAll.add("/actuator/**");
    permitAll.add("/favicon.ico");

    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

    http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.exceptionHandling(
        exception ->
            exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler));

    http.authorizeHttpRequests(
        request ->
            request
                .requestMatchers((permitAll.toArray(String[]::new)))
                .permitAll()
                .anyRequest()
                .authenticated());
    http.headers(
        headers ->
            headers
                .xssProtection(
                    xss ->
                        xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(cps -> cps.policyDirectives("script-src 'self'")));

    http.csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }
}
