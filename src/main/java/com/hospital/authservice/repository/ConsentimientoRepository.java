package com.hospital.authservice.repository;

import com.hospital.authservice.entity.Consentimiento;
import com.hospital.authservice.entity.Usuario;
import com.hospital.authservice.entity.TerminosCondiciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsentimientoRepository extends JpaRepository<Consentimiento, Long> {
    
    Optional<Consentimiento> findByUsuarioAndTerminosCondiciones(Usuario usuario, TerminosCondiciones terminosCondiciones);
    
    @Query("SELECT c FROM Consentimiento c WHERE c.usuario = :usuario AND c.terminosCondiciones.version = :version AND c.aceptado = true")
    Optional<Consentimiento> findConsentimientoAceptadoByUsuarioYVersion(@Param("usuario") Usuario usuario, @Param("version") Integer version);
    
    @Query("SELECT c FROM Consentimiento c WHERE c.usuario = :usuario AND c.terminosCondiciones.activo = true AND c.aceptado = true")
    Optional<Consentimiento> findUltimoConsentimientoAceptado(@Param("usuario") Usuario usuario);
}
