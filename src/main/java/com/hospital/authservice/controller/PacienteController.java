package com.hospital.authservice.controller;

import com.hospital.authservice.entity.Paciente;
import com.hospital.authservice.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteRepository pacienteRepository;

    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> obtenerPorRut(
            @PathVariable String rut) {

        
        Paciente paciente = pacienteRepository.findByPersona_NumeroDocumento(rut).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        return ResponseEntity.ok(paciente);
    }
}