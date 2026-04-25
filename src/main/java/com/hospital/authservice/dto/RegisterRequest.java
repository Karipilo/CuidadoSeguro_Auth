package com.hospital.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Request para registro de usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @Schema(description = "Nombre de usuario", example = "juan.perez")
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El username solo puede contener letras, números, puntos, guiones y guiones bajos")
    private String username;
    
    @Schema(description = "Contraseña", example = "Password123!")
    @NotBlank(message = "La contraseña es obligatoria")
    //@Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    //@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]", message = "La contraseña debe contener al menos una letra mayúscula, una minúscula, un número y un carácter especial")
    private String password;
    
    @Schema(description = "Email", example = "juan.perez@hospital.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
    
    @Schema(description = "Tipo de usuario", example = "PACIENTE", allowableValues = {"ADMIN", "MEDICO", "PACIENTE"})
    @NotBlank(message = "El tipo de usuario es obligatorio")
    private String tipoUsuario;
    
    @Schema(description = "Nombres", example = "Juan Carlos")
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;
    
    @Schema(description = "Apellidos", example = "Perez García")
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;
    
    @Schema(description = "Tipo de documento", example = "DNI")
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;
    
    @Schema(description = "Número de documento", example = "12345678")
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres")
    private String numeroDocumento;
    
    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
    private LocalDate fechaNacimiento;
    
    @Schema(description = "Género", example = "M", allowableValues = {"M", "F", "O"})
    private String genero;
    
    @Schema(description = "Teléfono", example = "+51987654321")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
    
    @Schema(description = "Dirección", example = "Av. Principal 123, Lima")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;
    
    @Schema(description = "Lista de roles", example = "[\"ROLE_PACIENTE\"]")
    @Builder.Default
    private List<String> roles = List.of("ROLE_PACIENTE");
    
    @Schema(description = "Aceptación de términos y condiciones")
    private Boolean aceptaTerminos;
    
    @Schema(description = "Versión de términos y condiciones aceptados")
    private Integer versionTerminos;
    
    // Campos específicos para médico
    @Schema(description = "Número de licencia médica", example = "CMP-12345")
    private String numeroLicencia;
    
    @Schema(description = "Especialidad médica", example = "Cardiología")
    private String especialidad;
    
    @Schema(description = "Subespecialidad médica", example = "Cardiología Intervencionista")
    private String subespecialidad;
    
    @Schema(description = "Universidad de graduación", example = "Universidad Nacional Mayor de San Marcos")
    private String universidad;
    
    @Schema(description = "Año de graduación", example = "2015")
    private Integer anioGraduacion;
    
    @Schema(description = "Años de experiencia", example = "8")
    private Integer experienciaAnios;
    
    @Schema(description = "Consultorio", example = "201")
    private String consultorio;
    
    @Schema(description = "Horario de trabajo", example = "Lunes a Viernes 8:00-14:00")
    private String horarioTrabajo;

    //Campos específicos para Tutor:
    @Schema(description = "Parentezco", example = "Tutor Legal")
    private Integer parentezco;

    @Schema(description = "Rut de los pacientes del tutor",example = "12345678-9")
    private List<String> pacientesRuts;
    
    // Campos específicos para paciente
    @Schema(description = "Historia clínica", example = "HC-001234")
    private String historiaClinica;
    
    @Schema(description = "Grupo sanguíneo", example = "O")
    private String grupoSanguineo;
    
    @Schema(description = "Factor RH", example = "+")
    private String factorRh;
    
    @Schema(description = "Alergias", example = "Penicilina, Ibuprofeno")
    private String alergias;
    
    @Schema(description = "Enfermedades crónicas", example = "Hipertensión, Diabetes")
    private String enfermedadesCronicas;
    
    @Schema(description = "Medicamentos actuales", example = "Losartán 50mg, Metformina 500mg")
    private String medicamentosActuales;
    
    @Schema(description = "Contacto de emergencia", example = "María Pérez")
    private String contactoEmergencia;
    
    @Schema(description = "Teléfono de emergencia", example = "+51987654322")
    private String telefonoEmergencia;
    
    @Schema(description = "Seguro médico", example = "EsSalud")
    private String seguroMedico;
    
    @Schema(description = "Número de póliza", example = "POL-001234")
    private String numeroPoliza;
}
