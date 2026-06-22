package com.hospital.authservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private Usuario usuario;
    private Role role;
    private Persona persona;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(1L)
                .nombre("ROLE_PACIENTE")
                .activo(true)
                .build();

        persona = Persona.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Perez")
                .tipoDocumento("DNI")
                .numeroDocumento("12345678")
                .activo(true)
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@test.com")
                .activo(true)
                .noBloqueado(true)
                .persona(persona)
                .roles(Set.of(role))
                .build();
    }

    @Test
    void testGetAuthorities() {
        var authorities = usuario.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_PACIENTE", authorities.iterator().next().getAuthority());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(usuario.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked_WhenNoBloqueado() {
        assertTrue(usuario.isAccountNonLocked());
    }

    @Test
    void testIsAccountNonLocked_WhenBloqueado() {
        usuario.setNoBloqueado(false);
        assertFalse(usuario.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(usuario.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled_WhenActivo() {
        assertTrue(usuario.isEnabled());
    }

    @Test
    void testIsEnabled_WhenInactivo() {
        usuario.setActivo(false);
        assertFalse(usuario.isEnabled());
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", usuario.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("encodedPassword", usuario.getPassword());
    }

    @Test
    void testBuilderDefaults() {
        Usuario u = Usuario.builder()
                .username("u")
                .password("p")
                .email("e@e.com")
                .build();
        assertTrue(u.getActivo());
        assertTrue(u.getNoBloqueado());
        assertEquals(0, u.getIntentosFallidos());
        assertNotNull(u.getFechaCreacion());
    }

    @Test
    void testSettersAndGetters() {
        usuario.setEmail("nuevo@test.com");
        assertEquals("nuevo@test.com", usuario.getEmail());

        usuario.setIntentosFallidos(3);
        assertEquals(3, usuario.getIntentosFallidos());
    }

    @Test
    void testGetAuthorities_MultipleRoles() {
        Role role2 = Role.builder().id(2L).nombre("ROLE_ADMIN").activo(true).build();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        roles.add(role2);
        usuario.setRoles(roles);

        var authorities = usuario.getAuthorities();
        assertEquals(2, authorities.size());
    }
}