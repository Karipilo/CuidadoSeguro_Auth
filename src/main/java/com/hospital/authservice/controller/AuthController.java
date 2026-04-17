package com.hospital.authservice.controller;

import com.hospital.authservice.dto.*;
import com.hospital.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse; // SOLO Swagger
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Gestión de autenticación y tokens JWT")
public class AuthController {

    private final AuthService authService;

    // ===================== LOGIN =====================

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica usuario y retorna JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Login attempt: {}", request.getUsername());

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // ===================== REGISTER =====================

    @PostMapping("/register")
    @Operation(summary = "Registro", description = "Crea un usuario nuevo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Register attempt: {}", request.getUsername());

        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===================== REFRESH =====================

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Renueva access token")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest request) {

        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    // ===================== LOGOUT =====================

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalida tokens")
    public ResponseEntity<ApiResponseDto<Void>> logout(
            @Valid @RequestBody LogoutRequest request) {

        authService.logout(request);

        return ResponseEntity.ok(
                ApiResponseDto.success(null, "Logout exitoso")
        );
    }

    // ===================== VALIDATE =====================

    @GetMapping("/validate")
    @Operation(summary = "Validar token")
    public ResponseEntity<ApiResponseDto<Boolean>> validate(
            @Parameter(description = "JWT a validar")
            @RequestParam String token) {

        boolean isValid = authService.validateToken(token);

        return ResponseEntity.ok(
                ApiResponseDto.success(isValid, "Resultado de validación")
        );
    }

    // ===================== HEALTH =====================

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<ApiResponseDto<String>> health() {

        return ResponseEntity.ok(
                ApiResponseDto.success("OK", "Auth service running")
        );
    }
}