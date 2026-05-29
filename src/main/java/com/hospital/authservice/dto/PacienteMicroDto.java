package com.hospital.authservice.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PacienteMicroDto {

    private String rut;
    private String nombre;
    private String apellido;
    private String email;
    private String diagnostico;
    private String alergias;
    private String direccion;
    private String telefono;
    private String genero;
    private LocalDate fechaNacimiento;
}