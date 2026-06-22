package com.hospital.authservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        refreshToken = RefreshToken.builder()
                .id(1L)
                .token("my-token")
                .fechaExpiracion(LocalDateTime.now().plusDays(1))
                .revocado(false)
                .usado(false)
                .build();
    }

    @Test
    void testIsExpirado_WhenNotExpired() {
        assertFalse(refreshToken.isExpirado());
    }

    @Test
    void testIsExpirado_WhenExpired() {
        refreshToken.setFechaExpiracion(LocalDateTime.now().minusDays(1));
        assertTrue(refreshToken.isExpirado());
    }

    @Test
    void testRevocar() {
        refreshToken.revocar();
        assertTrue(refreshToken.getRevocado());
        assertNotNull(refreshToken.getFechaRevocacion());
    }

    @Test
    void testMarcarComoUsado() {
        refreshToken.marcarComoUsado();
        assertTrue(refreshToken.getUsado());
        assertNotNull(refreshToken.getFechaUso());
    }

    @Test
    void testBuilderDefaults() {
        RefreshToken rt = RefreshToken.builder()
                .token("t")
                .fechaExpiracion(LocalDateTime.now().plusHours(1))
                .build();
        assertFalse(rt.getRevocado());
        assertFalse(rt.getUsado());
        assertNotNull(rt.getFechaCreacion());
    }

    @Test
    void testGettersAndSetters() {
        refreshToken.setToken("new-token");
        assertEquals("new-token", refreshToken.getToken());

        refreshToken.setId(99L);
        assertEquals(99L, refreshToken.getId());
    }
}