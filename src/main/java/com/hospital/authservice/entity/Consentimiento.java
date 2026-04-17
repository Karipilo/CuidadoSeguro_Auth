package com.hospital.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consentimientos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consentimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminos_id", nullable = false)
    private TerminosCondiciones terminosCondiciones;
    
    @Column(name = "aceptado", nullable = false)
    private Boolean aceptado;
    
    @Column(name = "fecha_aceptacion")
    private LocalDateTime fechaAceptacion;
    
    @Column(name = "direccion_ip", length = 50)
    private String direccionIp;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        if (aceptado != null && aceptado && fechaAceptacion == null) {
            fechaAceptacion = LocalDateTime.now();
        }
    }
}
