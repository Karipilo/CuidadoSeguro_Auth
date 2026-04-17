package com.hospital.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request para refrescar token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {
    
    @Schema(description = "Token de refresco", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;
}
