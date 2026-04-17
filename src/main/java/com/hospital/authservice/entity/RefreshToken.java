package com.hospital.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;
    
    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "revocado", nullable = false)
    @Builder.Default
    private Boolean revocado = false;
    
    @Column(name = "fecha_revocacion")
    private LocalDateTime fechaRevocacion;
    
    @Column(name = "usado", nullable = false)
    @Builder.Default
    private Boolean usado = false;
    
    @Column(name = "fecha_uso")
    private LocalDateTime fechaUso;
    
    @PreUpdate
    protected void onUpdate() {
        if (revocado != null && revocado && fechaRevocacion == null) {
            fechaRevocacion = LocalDateTime.now();
        }
        if (usado != null && usado && fechaUso == null) {
            fechaUso = LocalDateTime.now();
        }
    }
    
    public boolean isExpirado() {
        return fechaExpiracion.isBefore(LocalDateTime.now());
    }
    
    public void revocar() {
        this.revocado = true;
        this.fechaRevocacion = LocalDateTime.now();
    }
    
    public void marcarComoUsado() {
        this.usado = true;
        this.fechaUso = LocalDateTime.now();
    }
}
