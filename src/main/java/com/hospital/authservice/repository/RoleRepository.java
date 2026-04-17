package com.hospital.authservice.repository;

import com.hospital.authservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
    
    @Query("SELECT r FROM Role r WHERE r.nombre = :nombre AND r.activo = true")
    Optional<Role> findRoleActivoByNombre(@Param("nombre") String nombre);
}
