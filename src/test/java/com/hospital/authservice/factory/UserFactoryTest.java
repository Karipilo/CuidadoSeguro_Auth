package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;
import com.hospital.authservice.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFactoryTest {

    @Mock
    private AdminUser adminUser;
    
    @Mock
    private MedicoUser medicoUser;
    
    @Mock
    private PacienteUser pacienteUser;
    
    @InjectMocks
    private UserFactory userFactory;
    
    private RegisterRequest registerRequest;
    
    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .password("Password123!")
                .email("test@test.com")
                .tipoUsuario("PACIENTE")
                .nombres("Juan")
                .apellidos("Perez")
                .tipoDocumento("DNI")
                .numeroDocumento("12345678")
                .roles(List.of("ROLE_PACIENTE"))
                .build();
    }
    
    @Test
    void testCreateAdminUser() {
        // Given
        Usuario expectedUsuario = Usuario.builder().id(1L).username("admin").build();
        when(adminUser.crearUsuario(any(RegisterRequest.class))).thenReturn(expectedUsuario);
        
        // When
        User user = userFactory.createUser("ADMIN");
        
        // Then
        assertNotNull(user);
        assertEquals("ADMIN", user.getTipoUsuario());
        assertEquals(expectedUsuario, user.crearUsuario(registerRequest));
    }
    
    @Test
    void testCreateMedicoUser() {
        // Given
        Usuario expectedUsuario = Usuario.builder().id(2L).username("medico").build();
        when(medicoUser.crearUsuario(any(RegisterRequest.class))).thenReturn(expectedUsuario);
        
        // When
        User user = userFactory.createUser("MEDICO");
        
        // Then
        assertNotNull(user);
        assertEquals("MEDICO", user.getTipoUsuario());
        assertEquals(expectedUsuario, user.crearUsuario(registerRequest));
    }
    
    @Test
    void testCreatePacienteUser() {
        // Given
        Usuario expectedUsuario = Usuario.builder().id(3L).username("paciente").build();
        when(pacienteUser.crearUsuario(any(RegisterRequest.class))).thenReturn(expectedUsuario);
        
        // When
        User user = userFactory.createUser("PACIENTE");
        
        // Then
        assertNotNull(user);
        assertEquals("PACIENTE", user.getTipoUsuario());
        assertEquals(expectedUsuario, user.crearUsuario(registerRequest));
    }
    
    @Test
    void testCreateUnsupportedUserType() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userFactory.createUser("UNSUPPORTED"));
    }
    
    @Test
    void testGetSupportedUserTypes() {
        // When
        Set<String> supportedTypes = userFactory.getSupportedUserTypes();
        
        // Then
        assertNotNull(supportedTypes);
        assertEquals(3, supportedTypes.size());
        assertTrue(supportedTypes.contains("ADMIN"));
        assertTrue(supportedTypes.contains("MEDICO"));
        assertTrue(supportedTypes.contains("PACIENTE"));
    }
    
    @Test
    void testIsSupportedUserType() {
        // When & Then
        assertTrue(userFactory.isSupportedUserType("ADMIN"));
        assertTrue(userFactory.isSupportedUserType("MEDICO"));
        assertTrue(userFactory.isSupportedUserType("PACIENTE"));
        assertTrue(userFactory.isSupportedUserType("admin")); // Case insensitive
        assertFalse(userFactory.isSupportedUserType("UNSUPPORTED"));
    }
}
