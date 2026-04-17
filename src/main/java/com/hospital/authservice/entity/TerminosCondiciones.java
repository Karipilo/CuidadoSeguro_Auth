package com.hospital.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "terminos_condiciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminosCondiciones {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "version", nullable = false, unique = true)
    @Builder.Default
    private Integer version = 1;
    
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;
    
    @Lob
    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @Column(name = "fecha_vigencia", nullable = false)
    private LocalDateTime fechaVigencia;
    
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        if (fechaVigencia == null) {
            fechaVigencia = LocalDateTime.now();
        }
    }
}
