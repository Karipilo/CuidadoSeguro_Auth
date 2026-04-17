package com.hospital.authservice.repository;

import com.hospital.authservice.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    
    Optional<Medico> findByNumeroLicencia(String numeroLicencia);
    
    boolean existsByNumeroLicencia(String numeroLicencia);
    
    @Query("SELECT m FROM Medico m WHERE m.numeroLicencia = :numeroLicencia AND m.activo = true")
    Optional<Medico> findMedicoActivoByNumeroLicencia(@Param("numeroLicencia") String numeroLicencia);
}
