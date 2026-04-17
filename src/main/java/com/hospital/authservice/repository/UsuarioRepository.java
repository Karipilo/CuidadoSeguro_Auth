package com.hospital.authservice.repository;

import com.hospital.authservice.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsername(String username);
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.fechaUltimoLogin = :fechaLogin WHERE u.id = :usuarioId")
    void actualizarUltimoLogin(@Param("usuarioId") Long usuarioId, @Param("fechaLogin") LocalDateTime fechaLogin);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.intentosFallidos = :intentos WHERE u.id = :usuarioId")
    void actualizarIntentosFallidos(@Param("usuarioId") Long usuarioId, @Param("intentos") Integer intentos);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.noBloqueado = false WHERE u.id = :usuarioId")
    void bloquearUsuario(@Param("usuarioId") Long usuarioId);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.noBloqueado = true, u.intentosFallidos = 0 WHERE u.id = :usuarioId")
    void desbloquearUsuario(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND u.noBloqueado = true AND u.username = :username")
    Optional<Usuario> findUsuarioActivoByUsername(@Param("username") String username);
}
