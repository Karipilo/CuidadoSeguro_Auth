package com.hospital.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pacientes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "historia_clinica", nullable = false, unique = true, length = 50)
    private String historiaClinica;
    
    @Column(name = "grupo_sanguineo", length = 10)
    private String grupoSanguineo;
    
    @Column(name = "factor_rh", length = 5)
    private String factorRh;
    
    @Column(name = "alergias", length = 500)
    private String alergias;
    
    @Column(name = "enfermedades_cronicas", length = 500)
    private String enfermedadesCronicas;
    
    @Column(name = "medicamentos_actuales", length = 500)
    private String medicamentosActuales;
    
    @Column(name = "contacto_emergencia", length = 100)
    private String contactoEmergencia;
    
    @Column(name = "telefono_emergencia", length = 20)
    private String telefonoEmergencia;
    
    @Column(name = "prevision", length = 100)
    private String prevision;
    
    
    
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
    
    @ManyToOne // o OneToOne, depende de tu diseño
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
