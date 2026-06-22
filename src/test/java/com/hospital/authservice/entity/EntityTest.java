package com.hospital.authservice.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    // ===== ROLE =====

    @Test
    void testRoleBuilder() {
        Role role = Role.builder()
                .id(1L)
                .nombre("ROLE_ADMIN")
                .descripcion("Administrador")
                .activo(true)
                .build();

        assertEquals(1L, role.getId());
        assertEquals("ROLE_ADMIN", role.getNombre());
        assertEquals("Administrador", role.getDescripcion());
        assertTrue(role.getActivo());
    }

    @Test
    void testRoleSetters() {
        Role role = new Role();
        role.setId(2L);
        role.setNombre("ROLE_USER");
        role.setDescripcion("Usuario");
        role.setActivo(false);

        assertEquals(2L, role.getId());
        assertEquals("ROLE_USER", role.getNombre());
        assertFalse(role.getActivo());
    }

    @Test
    void testRoleDefaultActivo() {
        Role role = Role.builder().nombre("ROLE_TEST").build();
        assertTrue(role.getActivo());
    }

    // ===== PERSONA =====

    @Test
    void testPersonaBuilder() {
        Persona persona = Persona.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Perez")
                .tipoDocumento("DNI")
                .numeroDocumento("12345678")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .genero("M")
                .telefono("123456789")
                .email("juan@test.com")
                .direccion("Calle 123")
                .activo(true)
                .build();

        assertEquals("Juan", persona.getNombres());
        assertEquals("Perez", persona.getApellidos());
        assertEquals("DNI", persona.getTipoDocumento());
        assertEquals("12345678", persona.getNumeroDocumento());
        assertEquals(LocalDate.of(1990, 1, 1), persona.getFechaNacimiento());
        assertEquals("M", persona.getGenero());
        assertEquals("123456789", persona.getTelefono());
        assertEquals("juan@test.com", persona.getEmail());
        assertEquals("Calle 123", persona.getDireccion());
        assertTrue(persona.getActivo());
    }

    @Test
    void testPersonaSetters() {
        Persona persona = new Persona();
        persona.setNombres("Maria");
        persona.setApellidos("Garcia");
        persona.setActivo(false);

        assertEquals("Maria", persona.getNombres());
        assertEquals("Garcia", persona.getApellidos());
        assertFalse(persona.getActivo());
    }

    @Test
    void testPersonaDefaultActivo() {
        Persona persona = Persona.builder().nombres("Test").build();
        assertTrue(persona.getActivo());
    }

    // ===== PACIENTE =====

    @Test
    void testPacienteBuilder() {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .historiaClinica("HC-001")
                .grupoSanguineo("A+")
                .factorRh("+")
                .alergias("Penicilina")
                .enfermedadesCronicas("Diabetes")
                .medicamentosActuales("Insulina")
                .contactoEmergencia("Maria")
                .telefonoEmergencia("999")
                .prevision("FONASA")
                .activo(true)
                .build();

        assertEquals("HC-001", paciente.getHistoriaClinica());
        assertEquals("A+", paciente.getGrupoSanguineo());
        assertEquals("+", paciente.getFactorRh());
        assertEquals("Penicilina", paciente.getAlergias());
        assertEquals("Diabetes", paciente.getEnfermedadesCronicas());
        assertEquals("Insulina", paciente.getMedicamentosActuales());
        assertEquals("Maria", paciente.getContactoEmergencia());
        assertEquals("999", paciente.getTelefonoEmergencia());
        assertEquals("FONASA", paciente.getPrevision());
        assertTrue(paciente.getActivo());
    }

    @Test
    void testPacienteSetters() {
        Paciente paciente = new Paciente();
        paciente.setHistoriaClinica("HC-999");
        paciente.setGrupoSanguineo("O-");
        paciente.setActivo(false);

        assertEquals("HC-999", paciente.getHistoriaClinica());
        assertEquals("O-", paciente.getGrupoSanguineo());
        assertFalse(paciente.getActivo());
    }

    @Test
    void testPacienteDefaultActivo() {
        Paciente paciente = Paciente.builder().historiaClinica("HC-X").build();
        assertTrue(paciente.getActivo());
        assertNotNull(paciente.getFechaCreacion());
    }

    // ===== TUTOR =====

    @Test
    void testTutorBuilder() {
        Tutor tutor = Tutor.builder()
                .id(1L)
                .pacientesRuts(java.util.List.of("12345678", "87654321"))
                .build();

        assertEquals(1L, tutor.getId());
        assertEquals(2, tutor.getPacientesRuts().size());
        assertTrue(tutor.getPacientesRuts().contains("12345678"));
    }

    @Test
    void testTutorSetters() {
        Tutor tutor = new Tutor();
        tutor.setId(5L);
        tutor.setPacientesRuts(java.util.List.of("11111111"));

        assertEquals(5L, tutor.getId());
        assertEquals(1, tutor.getPacientesRuts().size());
    }

    // ===== TOKEN BLACKLIST =====

    @Test
    void testTokenBlacklistBuilder() {
        TokenBlacklist tb = TokenBlacklist.builder()
                .id(1L)
                .token("some-token")
                .fechaExpiracion(LocalDateTime.now().plusHours(1))
                .motivo("Logout")
                .build();

        assertEquals("some-token", tb.getToken());
        assertEquals("Logout", tb.getMotivo());
    }
}