package com.hospital.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request para inicio de sesión")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @Schema(description = "Nombre de usuario", example = "juan.perez")
    @NotBlank(message = "El username es obligatorio")
    private String username;
    
    @Schema(description = "Contraseña", example = "Password123!")
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
