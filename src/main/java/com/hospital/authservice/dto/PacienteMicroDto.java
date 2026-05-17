package com.hospital.authservice.dto;

import lombok.Data;

@Data
public class PacienteMicroDto {

    private String rut;
    private String nombre;
    private String apellido;
    private String email;
}