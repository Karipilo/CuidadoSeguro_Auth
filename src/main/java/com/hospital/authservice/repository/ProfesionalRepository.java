package com.hospital.authservice.repository;

import com.hospital.authservice.entity.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    
    Optional<Profesional> findByNumeroLicencia(String numeroLicencia);
    
    boolean existsByNumeroLicencia(String numeroLicencia);
    
    @Query("SELECT m FROM Profesional m WHERE m.numeroLicencia = :numeroLicencia AND m.activo = true")
    Optional<Profesional> findProfesionalActivoByNumeroLicencia(@Param("numeroLicencia") String numeroLicencia);
}
