package com.hospital.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Schema(description = "Response de autenticación con tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Token de refresco", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Tiempo de expiración del access token en segundos", example = "900")
    private Long expiresIn;

    @Schema(description = "Información del usuario")
    private UserInfo userInfo;

    @Schema(description = "Mensaje de respuesta")
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {

        @Schema(description = "ID del usuario", example = "1")
        private Long id;

        @Schema(description = "Nombre de usuario", example = "juan.perez")
        private String username;

        @Schema(description = "Email del usuario", example = "juan.perez@hospital.com")
        private String email;

        @Schema(description = "Nombre completo", example = "Juan Carlos Pérez García")
        private String nombreCompleto;

        @Schema(description = "Tipo de usuario", example = "PACIENTE")
        private String tipoUsuario;

        private List<String> pacientesRuts;

        @Schema(description = "Teléfono del usuario")
        private String telefono;

        @Schema(description = "Dirección del usuario")
        private String direccion;

        @Schema(description = "Género del usuario", example = "MASCULINO")
        private String genero;

        @Schema(description = "Roles del usuario", example = "[\"ROLE_PACIENTE\"]")
        private java.util.List<String> roles;

        @Schema(description = "Fecha de nacimiento", example = "1990-01-01")
        private String fechaNacimiento;

    }
}