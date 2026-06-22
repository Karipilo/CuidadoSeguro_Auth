package com.hospital.authservice.security;

import com.hospital.authservice.entity.Persona;
import com.hospital.authservice.entity.Role;
import com.hospital.authservice.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "miClaveSecretaMuyLargaYSeguraParaJWT123456789");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 900L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800L);

        Role role = Role.builder()
                .id(1L)
                .nombre("ROLE_PACIENTE")
                .activo(true)
                .build();

        Persona persona = Persona.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Perez")
                .activo(true)
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .username("testuser")
                .password("encoded")
                .email("test@test.com")
                .activo(true)
                .noBloqueado(true)
                .persona(persona)
                .roles(Set.of(role))
                .build();
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(new HashMap<>(), usuario);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateTokenSimple() {
        String token = jwtService.generateToken(usuario);
        assertNotNull(token);
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtService.generateRefreshToken(new HashMap<>(), usuario, "test@test.com");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken(new HashMap<>(), usuario);
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testGetUserIdFromToken() {
        String token = jwtService.generateToken(new HashMap<>(), usuario);
        Long userId = jwtService.getUserIdFromToken(token);
        assertEquals(1L, userId);
    }

    @Test
    void testGetEmailFromToken() {
        String token = jwtService.generateToken(new HashMap<>(), usuario);
        String email = jwtService.getEmailFromToken(token);
        assertEquals("test@test.com", email);
    }

    @Test
    void testIsTokenValid_ValidToken() {
        String token = jwtService.generateToken(new HashMap<>(), usuario);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void testIsTokenValid_InvalidToken() {
        assertFalse(jwtService.isTokenValid("invalid.token.here"));
    }

    @Test
    void testIsTokenValid_WithUserDetails() {
        String token = jwtService.generateToken(new HashMap<>(), usuario);
        assertTrue(jwtService.isTokenValid(token, usuario));
    }

    @Test
    void testIsTokenValid_WithBearerPrefix() {
        String token = jwtService.generateToken(new HashMap<>(), usuario);
        assertTrue(jwtService.isTokenValid("Bearer " + token));
    }

    @Test
    void testGetExpirationDate() {
        String token = jwtService.generateToken(new HashMap<>(), usuario);
        LocalDateTime expiration = jwtService.getExpirationDate(token);
        assertNotNull(expiration);
        assertTrue(expiration.isAfter(LocalDateTime.now()));
    }

    @Test
    void testGetJwtExpiration() {
        assertEquals(900L, jwtService.getJwtExpiration());
    }

    @Test
    void testGetRefreshExpiration() {
        assertEquals(604800L, jwtService.getRefreshExpiration());
    }

    @Test
    void testExtractExpiration() {
        String token = jwtService.generateToken(new HashMap<>(), usuario);
        assertNotNull(jwtService.extractExpiration(token));
    }
}