package com.hospital.authservice.repository;

import com.hospital.authservice.entity.TerminosCondiciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerminosCondicionesRepository extends JpaRepository<TerminosCondiciones, Long> {
    
    Optional<TerminosCondiciones> findByVersion(Integer version);
    
    @Query("SELECT tc FROM TerminosCondiciones tc WHERE tc.activo = true ORDER BY tc.version DESC")
    List<TerminosCondiciones> findTerminosActivosOrderByVersionDesc();
    
    @Query("SELECT tc FROM TerminosCondiciones tc WHERE tc.activo = true ORDER BY tc.version DESC")
    Optional<TerminosCondiciones> findUltimaVersionActiva();
    
    boolean existsByVersion(Integer version);
}
