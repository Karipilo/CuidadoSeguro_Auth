package com.hospital.authservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret:miClaveSecretaMuyLargaYSeguraParaJWT123456789}")
    private String jwtSecret;

    @Value("${jwt.expiration:900}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800}")
    private Long refreshExpiration;

    // =========================
    // KEY
    // =========================
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // =========================
    // EXTRACT CLAIMS
    // =========================
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // =========================
    // USER DATA (CLEAN)
    // =========================

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long getUserIdFromToken(String token) {
        Object id = extractAllClaims(token).get("userId");
        return id != null ? Long.valueOf(id.toString()) : null;
    }

    public String getEmailFromToken(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    // =========================
    // TOKEN GENERATION
    // =========================

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>(extraClaims);

        try {
            if (userDetails instanceof com.hospital.authservice.entity.Usuario usuario) {

                claims.put("userId", usuario.getId()); // 🔥 CLAVE NUEVA
                claims.put("email", usuario.getEmail());

                return Jwts.builder()
                        .setClaims(claims)
                        .setSubject(usuario.getUsername()) // username limpio (NO cifrado)
                        .setIssuedAt(new Date())
                        .setExpiration(Date.from(
                                Instant.now().plus(jwtExpiration, ChronoUnit.SECONDS)))
                        .signWith(getSigningKey())
                        .compact();

            } else {

                claims.put("userId", null);

                return Jwts.builder()
                        .setClaims(claims)
                        .setSubject(userDetails.getUsername())
                        .setIssuedAt(new Date())
                        .setExpiration(Date.from(
                                Instant.now().plus(jwtExpiration, ChronoUnit.SECONDS)))
                        .signWith(getSigningKey())
                        .compact();
            }

        } catch (Exception e) {
            log.error("Error generando token", e);
            throw new RuntimeException("Error generando token");
        }
    }

    // =========================
    // REFRESH TOKEN
    // =========================

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails, String email) {

        Map<String, Object> claims = new HashMap<>(extraClaims);

        if (userDetails instanceof com.hospital.authservice.entity.Usuario usuario) {
            claims.put("userId", usuario.getId());
        }

        claims.put("email", email);
        claims.put("type", "refresh");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(
                        Instant.now().plus(refreshExpiration, ChronoUnit.SECONDS)))
                .signWith(getSigningKey())
                .compact();
    }

    // =========================
    // VALIDATION
    // =========================

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        try {
            String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token);

        } catch (Exception e) {
            log.error("Token inválido", e.getMessage());
            return false;
        }
    }

    public boolean isTokenValid(String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    // =========================
    // UTIL
    // =========================

    public LocalDateTime getExpirationDate(String token) {
        return extractExpiration(token)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Long getJwtExpiration() {
        return jwtExpiration;
    }

    public Long getRefreshExpiration() {
        return refreshExpiration;
    }
}