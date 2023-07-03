package com.refresher.security.config;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecretKeyConfiguration {

  private final String secretKey;

  public SecretKeyConfiguration(@Value("${app.secret-key}") final String secretKey) {
    this.secretKey = secretKey;
  }

  public SecretKey getSecretKeyForSigning() {
    return hmacShaKeyFor(secretKey.getBytes());
  }
}
