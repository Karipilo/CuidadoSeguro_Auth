package com.hospital.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "profesionales")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profesional {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_licencia", nullable = false, unique = true, length = 50)
    private String numeroLicencia;
    
    @Column(name = "profesion", nullable = false, length = 100)
    private String profesion;
    
    @Column(name = "especialidad", nullable = false, length = 100)
    private String especialidad;
    
    @Column(name = "subespecialidad", length = 100)
    private String subespecialidad;
    
    @Column(name = "universidad", length = 100)
    private String universidad;
    
    @Column(name = "anio_graduacion")
    private Integer anioGraduacion;
    
    @Column(name = "experiencia_anios")
    @Builder.Default
    private Integer experienciaAnios = 0;
    
    @Column(name = "institucion", length = 50)
    private String institucion;
    
    @Column(name = "horas_semanales", length = 200)
    private String horasSemanales;
    
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
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
