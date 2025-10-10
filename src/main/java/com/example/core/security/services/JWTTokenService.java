package com.example.core.security.services;

import com.example.core.security.models.JwtProperties;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JWTTokenService {
  @NonNull JwtProperties jwtProperties;

  public Jwt getJwt(final String token) {
    return this.jwtDecoder().decode(token);
  }

  @Bean
  protected JwtDecoder jwtDecoder() {
    if (jwtProperties.emptyJwkSetUri()) {
      return NimbusJwtDecoder.withPublicKey(jwtProperties.getPublicKey()).build();
    } else {
      return NimbusJwtDecoder.withJwkSetUri(jwtProperties.getJwkSetUri()).build();
    }
  }

  @Bean
  protected JwtEncoder jwtEncoder() {
    JWK jwk =
        (new RSAKey.Builder(jwtProperties.getPublicKey()))
            .privateKey(jwtProperties.getPrivateKey())
            .build();
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSource);
  }

  public String createAccessToken(String subject, Map<String, Object> claims) {
    Instant now = Instant.now();
    JwtClaimsSet.Builder claimBuilder = JwtClaimsSet.builder();
    claimBuilder
        .subject(subject)
        .issuer(jwtProperties.getIssuer())
        .issuedAt(now)
        .expiresAt(now.plusSeconds(jwtProperties.getJwtAccessTokenExpirationS()));
    Objects.requireNonNull(claimBuilder);
    claims.forEach(claimBuilder::claim);
    return jwtEncoder().encode(JwtEncoderParameters.from(claimBuilder.build())).getTokenValue();
  }

  public String createRefreshToken(String subject, Map<String, Object> claims) {
    Instant now = Instant.now();
    JwtClaimsSet.Builder claimBuilder = JwtClaimsSet.builder();
    claimBuilder
        .issuer(this.jwtProperties.getIssuer())
        .issuedAt(now)
        .expiresAt(now.plusSeconds(this.jwtProperties.getJwtRefreshExpirationS()))
        .subject(subject);
    Objects.requireNonNull(claimBuilder);
    claims.forEach(claimBuilder::claim);
    return this.jwtEncoder()
        .encode(JwtEncoderParameters.from(claimBuilder.build()))
        .getTokenValue();
  }
}
