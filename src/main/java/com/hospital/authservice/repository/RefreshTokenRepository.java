package com.hospital.authservice.repository;

import com.hospital.authservice.entity.RefreshToken;
import com.hospital.authservice.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    Optional<RefreshToken> findByUsuarioAndRevocadoFalseAndUsadoFalse(Usuario usuario);
    
    List<RefreshToken> findByUsuarioAndRevocadoFalse(Usuario usuario);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.fechaExpiracion < :fecha")
    void eliminarTokensExpirados(@Param("fecha") LocalDateTime fecha);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revocado = true WHERE rt.usuario = :usuario")
    void revocarTodosTokensUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.usuario = :usuario AND rt.revocado = false AND rt.usado = false AND rt.fechaExpiracion > :fecha")
    Optional<RefreshToken> findTokenValidoByUsuario(@Param("usuario") Usuario usuario, @Param("fecha") LocalDateTime fecha);
    
    boolean existsByToken(String token);
}
