package com.hospital.authservice.repository;

import com.hospital.authservice.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    
    Optional<TokenBlacklist> findByToken(String token);
    
    boolean existsByToken(String token);
    
    @Modifying
    @Query("DELETE FROM TokenBlacklist tb WHERE tb.fechaExpiracion < :fecha")
    void eliminarTokensExpirados(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT tb FROM TokenBlacklist tb WHERE tb.token = :token AND tb.fechaExpiracion > :fecha")
    Optional<TokenBlacklist> findTokenValidoByToken(@Param("token") String token, @Param("fecha") LocalDateTime fecha);
}
