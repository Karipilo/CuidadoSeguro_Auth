package com.hospital.authservice.security;

import com.hospital.authservice.entity.Persona;
import com.hospital.authservice.entity.Role;
import com.hospital.authservice.entity.Usuario;
import com.hospital.authservice.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Role role = Role.builder()
                .id(1L).nombre("ROLE_PACIENTE").activo(true).build();

        Persona persona = Persona.builder()
                .id(1L).nombres("Juan").apellidos("Perez").activo(true).build();

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
    void testLoadUserByUsername_Success() {
        when(usuarioRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(usuario));

        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(usuarioRepository.findByUsername("noexiste"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("noexiste"));
    }
}