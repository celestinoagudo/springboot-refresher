package com.refresher.security.service;

import com.refresher.security.config.SecretKeyConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.collect.Maps.newHashMap;
import static io.jsonwebtoken.Jwts.parserBuilder;

@Service
public class JsonWebTokenService {

    private final SecretKeyConfiguration secretKeyConfiguration;
    private final Long expiresAfterWeeks;

    @Autowired
    public JsonWebTokenService(
            final SecretKeyConfiguration secretKeyConfiguration,
            final @Value("${app.token.expires.after.weeks}") Long expiresAfterWeeks) {
        this.secretKeyConfiguration = secretKeyConfiguration;
        this.expiresAfterWeeks = expiresAfterWeeks;
    }

    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(final String token, final Function<Claims, T> claimResolver) {
        var claims = extractClaims(token);
        return claimResolver.apply(claims);
    }

    public String generateToken(final UserDetails userDetails) {
        return generateToken(newHashMap(), userDetails);
    }

    public String generateToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(LocalDateTime.now()
                        .plusWeeks(expiresAfterWeeks)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()))
                .signWith(secretKeyConfiguration.getSecretKeyForSigning(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        final var username = extractUsername(token);
        return StringUtils.equals(username, userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractClaims(final String token) {
        return parserBuilder()
                .setSigningKey(secretKeyConfiguration.getSecretKeyForSigning())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
