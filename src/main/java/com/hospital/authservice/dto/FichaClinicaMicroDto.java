package com.hospital.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FichaClinicaMicroDto {

    private String nombrePaciente;
    private String rutPaciente;
    private Integer edad;
    private String diagnostico;
    private String alergias;
    private String observaciones;
    private String genero;
}