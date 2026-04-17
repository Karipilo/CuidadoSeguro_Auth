package com.hospital.authservice.repository;

import com.hospital.authservice.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    
    Optional<Persona> findByNumeroDocumento(String numeroDocumento);
    
    boolean existsByNumeroDocumento(String numeroDocumento);
    
    @Query("SELECT p FROM Persona p WHERE p.numeroDocumento = :numeroDocumento AND p.activo = true")
    Optional<Persona> findPersonaActivaByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);
}
