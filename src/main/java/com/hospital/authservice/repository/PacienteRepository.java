package com.hospital.authservice.repository;

import com.hospital.authservice.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    
    List<Paciente> findByPersonaNumeroDocumentoIn(List<String> ruts);


    Optional<Paciente> findByHistoriaClinica(String historiaClinica);
    
    boolean existsByHistoriaClinica(String historiaClinica);
    
    @Query("SELECT p FROM Paciente p WHERE p.historiaClinica = :historiaClinica AND p.activo = true")
    Optional<Paciente> findPacienteActivoByHistoriaClinica(@Param("historiaClinica") String historiaClinica);

    Optional<Paciente> findByPersona_NumeroDocumento(String numeroDocumento);
}
