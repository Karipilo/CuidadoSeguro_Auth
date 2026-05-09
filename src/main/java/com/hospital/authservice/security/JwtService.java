package com.hospital.authservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hospital.authservice.utils.CryptoUtil;

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

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String extractUsername(String token) {
        String encrypted = extractClaim(token, Claims::getSubject);
        return CryptoUtil.decrypt(encrypted);
    }

    public String extractEmail(String token) {
        String encrypted = extractAllClaims(token).get("email", String.class);
        return CryptoUtil.decrypt(encrypted);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>(extraClaims);

        try {
            Object principal = userDetails;

            if (principal instanceof com.hospital.authservice.entity.Usuario usuario) {

                claims.put("username", CryptoUtil.encrypt(usuario.getUsername()));
                claims.put("email", CryptoUtil.encrypt(usuario.getEmail()));

            } else {
                // fallback 
                claims.put("username", CryptoUtil.encrypt(userDetails.getUsername()));
            }

        } catch (Exception e) {
            log.warn("No se pudieron encriptar datos: {}", e.getMessage());
        }

        String encryptedSubject = CryptoUtil.encrypt(userDetails.getUsername());

        return Jwts.builder()
                .addClaims(claims)
                .subject(encryptedSubject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(Instant.now().plus(jwtExpiration, ChronoUnit.SECONDS)))
                .signWith(getSigningKey())
                .compact();
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            String email,
            long expirationSeconds,
            String type
    ) {

        Map<String, Object> claims = new HashMap<>(extraClaims);

        log.debug("Generando {} token para usuario: {}", type, userDetails.getUsername());

        try {
            claims.put("username", CryptoUtil.encrypt(userDetails.getUsername()));
            claims.put("email", CryptoUtil.encrypt(email));
        } catch (Exception e) {
            log.warn("Error encriptando datos: {}", e.getMessage());
        }

        claims.put("type", type);

        return Jwts.builder()
                .addClaims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(expirationSeconds, ChronoUnit.SECONDS)))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, String email) {
        return buildToken(extraClaims, userDetails, email, jwtExpiration, "access");
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails, String email) {
        return buildToken(extraClaims, userDetails, email, refreshExpiration, "refresh");
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Error validando token 1: {}", e.getMessage());
            log.error("Token inválido: \"{}\"", token);
            return false;
        }
    }

    public Boolean isTokenValid(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error validando token 2: {}", e.getMessage());
            log.error("Token inválido: \"{}\"", token);
            return false;
        }
    }

    public LocalDateTime getExpirationDate(String token) {
        Date expiration = extractExpiration(token);
        return expiration.toInstant()
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