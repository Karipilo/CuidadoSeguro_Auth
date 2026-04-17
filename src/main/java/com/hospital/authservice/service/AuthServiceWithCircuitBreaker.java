package com.hospital.authservice.service;

import com.hospital.authservice.dto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceWithCircuitBreaker {
    
    private final AuthService authService;
    
    private static final String AUTH_CIRCUIT_BREAKER = "authCircuitBreaker";
    
    @CircuitBreaker(
            name = AUTH_CIRCUIT_BREAKER,
            fallbackMethod = "fallbackLogin"
    )
    public AuthResponse loginWithCircuitBreaker(LoginRequest request) {
        log.debug("Ejecutando login con circuit breaker para usuario: {}", request.getUsername());
        return authService.login(request);
    }
    
    @CircuitBreaker(
            name = AUTH_CIRCUIT_BREAKER,
            fallbackMethod = "fallbackRegister"
    )
    public AuthResponse registerWithCircuitBreaker(RegisterRequest request) {
        log.debug("Ejecutando registro con circuit breaker para usuario: {}", request.getUsername());
        return authService.register(request);
    }
    
    @CircuitBreaker(
            name = AUTH_CIRCUIT_BREAKER,
            fallbackMethod = "fallbackRefreshToken"
    )
    public AuthResponse refreshTokenWithCircuitBreaker(RefreshRequest request) {
        log.debug("Ejecutando refresh token con circuit breaker");
        return authService.refreshToken(request);
    }
    
    @CircuitBreaker(
            name = AUTH_CIRCUIT_BREAKER,
            fallbackMethod = "fallbackLogout"
    )
    public ApiResponseDto<Void> logoutWithCircuitBreaker(LogoutRequest request) {
        log.debug("Ejecutando logout con circuit breaker");
        return authService.logout(request);
    }
    
    // Métodos fallback
    public AuthResponse fallbackLogin(LoginRequest request, Exception ex) {
        log.error("Fallback login para usuario {} debido a: {}", request.getUsername(), ex.getMessage());
        return AuthResponse.builder()
                .message("Servicio de autenticación temporalmente no disponible. Intente más tarde.")
                .build();
    }
    
    public AuthResponse fallbackRegister(RegisterRequest request, Exception ex) {
        log.error("Fallback register para usuario {} debido a: {}", request.getUsername(), ex.getMessage());
        return AuthResponse.builder()
                .message("Servicio de registro temporalmente no disponible. Intente más tarde.")
                .build();
    }
    
    public AuthResponse fallbackRefreshToken(RefreshRequest request, Exception ex) {
        log.error("Fallback refresh token debido a: {}", ex.getMessage());
        return AuthResponse.builder()
                .message("Servicio de refresh token temporalmente no disponible. Intente más tarde.")
                .build();
    }
    
    public ApiResponseDto<Void> fallbackLogout(LogoutRequest request, Exception ex) {
        log.error("Fallback logout debido a: {}", ex.getMessage());
        return ApiResponseDto.error("Servicio de logout temporalmente no disponible. Intente más tarde.");
    }
}
