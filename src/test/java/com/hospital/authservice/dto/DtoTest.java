package com.hospital.authservice.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    // ===== ApiResponseDto =====

    @Test
    void testApiResponseSuccess() {
        ApiResponseDto<String> response = ApiResponseDto.success("data");
        assertTrue(response.getSuccess());
        assertEquals("data", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testApiResponseSuccessWithMessage() {
        ApiResponseDto<String> response = ApiResponseDto.success("data", "ok");
        assertTrue(response.getSuccess());
        assertEquals("data", response.getData());
        assertEquals("ok", response.getMessage());
    }

    @Test
    void testApiResponseError() {
        ApiResponseDto<Void> response = ApiResponseDto.error("error msg");
        assertFalse(response.getSuccess());
        assertEquals("error msg", response.getMessage());
    }

    @Test
    void testApiResponseErrorWithCode() {
        ApiResponseDto<Void> response = ApiResponseDto.error("error", "ERR_CODE");
        assertFalse(response.getSuccess());
        assertEquals("ERR_CODE", response.getErrorCode());
    }

    // ===== AuthResponse =====

    @Test
    void testAuthResponseBuilder() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .tokenType("Bearer")
                .expiresIn(900L)
                .message("Login exitoso")
                .build();

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(900L, response.getExpiresIn());
        assertEquals("Login exitoso", response.getMessage());
    }

    @Test
    void testAuthResponseUserInfo() {
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(1L)
                .username("user")
                .email("user@test.com")
                .nombreCompleto("Juan Perez")
                .roles(List.of("ROLE_PACIENTE"))
                .tipoUsuario("PACIENTE")
                .build();

        assertEquals(1L, userInfo.getId());
        assertEquals("user", userInfo.getUsername());
        assertEquals("user@test.com", userInfo.getEmail());
        assertEquals("Juan Perez", userInfo.getNombreCompleto());
        assertEquals(1, userInfo.getRoles().size());
    }

    // ===== LoginRequest =====

    @Test
    void testLoginRequestBuilder() {
        LoginRequest req = LoginRequest.builder()
                .username("user")
                .password("pass")
                .build();

        assertEquals("user", req.getUsername());
        assertEquals("pass", req.getPassword());
    }

    // ===== RegisterRequest =====

    @Test
    void testRegisterRequestBuilder() {
        RegisterRequest req = RegisterRequest.builder()
                .username("newuser")
                .password("Password123!")
                .email("new@test.com")
                .tipoUsuario("PACIENTE")
                .nombres("Juan")
                .apellidos("Perez")
                .tipoDocumento("DNI")
                .numeroDocumento("12345678")
                .grupoSanguineo("A+")
                .factorRh("+")
                .alergias("Ninguna")
                .enfermedadesCronicas("Ninguna")
                .contactoEmergencia("Maria")
                .telefonoEmergencia("999")
                .prevision("FONASA")
                .aceptaTerminos(true)
                .roles(List.of("ROLE_PACIENTE"))
                .build();

        assertEquals("newuser", req.getUsername());
        assertEquals("PACIENTE", req.getTipoUsuario());
        assertEquals("A+", req.getGrupoSanguineo());
        assertTrue(req.getAceptaTerminos());
    }

    @Test
    void testRegisterRequestProfesionalFields() {
        RegisterRequest req = RegisterRequest.builder()
                .username("doctor")
                .password("Password123!")
                .email("doc@test.com")
                .tipoUsuario("PROFESIONAL")
                .nombres("Carlos")
                .apellidos("Gomez")
                .tipoDocumento("DNI")
                .numeroDocumento("87654321")
                .numeroLicencia("LIC-001")
                .especialidad("Cardiología")
                .profesion("Médico")
                .universidad("Universidad de Chile")
                .anioGraduacion(2010)
                .experienciaAnios(10)
                .subespecialidad("Intervencionista")
                .institucion("Hospital")
                .horasSemanales("40")
                .build();

        assertEquals("LIC-001", req.getNumeroLicencia());
        assertEquals("Cardiología", req.getEspecialidad());
        assertEquals(2010, req.getAnioGraduacion());
        assertEquals(10, req.getExperienciaAnios());
    }

    @Test
    void testRegisterRequestTutorFields() {
        RegisterRequest req = RegisterRequest.builder()
                .username("tutor")
                .password("Password123!")
                .email("tutor@test.com")
                .tipoUsuario("TUTOR")
                .nombres("Maria")
                .apellidos("Lopez")
                .tipoDocumento("DNI")
                .numeroDocumento("11111111")
                .pacientesRuts(List.of("12345678", "87654321"))
                .build();

        assertEquals(2, req.getPacientesRuts().size());
    }

    @Test
    void testRegisterRequestDefaultRoles() {
        RegisterRequest req = RegisterRequest.builder()
                .username("u")
                .password("p")
                .email("e@e.com")
                .tipoUsuario("PACIENTE")
                .nombres("n")
                .apellidos("a")
                .tipoDocumento("DNI")
                .numeroDocumento("123")
                .build();

        assertNotNull(req.getRoles());
        assertFalse(req.getRoles().isEmpty());
    }

    // ===== RefreshRequest =====

    @Test
    void testRefreshRequestBuilder() {
        RefreshRequest req = RefreshRequest.builder()
                .refreshToken("my-token")
                .build();
        assertEquals("my-token", req.getRefreshToken());
    }

    // ===== LogoutRequest =====

    @Test
    void testLogoutRequestBuilder() {
        LogoutRequest req = LogoutRequest.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .build();

        assertEquals("access", req.getAccessToken());
        assertEquals("refresh", req.getRefreshToken());
    }

    // ===== PacienteMicroDto =====

    @Test
    void testPacienteMicroDto() {
        PacienteMicroDto dto = new PacienteMicroDto();
        dto.setRut("12345678");
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setEmail("j@test.com");
        dto.setAlergias("Ninguna");
        dto.setDireccion("Calle 1");
        dto.setTelefono("999");
        dto.setGenero("M");
        dto.setFechaNacimiento(LocalDate.of(1990, 1, 1));

        assertEquals("12345678", dto.getRut());
        assertEquals("Juan", dto.getNombre());
        assertEquals("Perez", dto.getApellido());
    }

    // ===== FichaClinicaMicroDto =====

    @Test
    void testFichaClinicaMicroDto() {
        FichaClinicaMicroDto dto = new FichaClinicaMicroDto();
        dto.setNombrePaciente("Juan Perez");
        dto.setRutPaciente("12345678");
        dto.setEdad(30);
        dto.setDiagnostico("Sano");
        dto.setAlergias("Ninguna");
        dto.setObservaciones("Sin observaciones");
        dto.setGenero("M");

        assertEquals("Juan Perez", dto.getNombrePaciente());
        assertEquals("12345678", dto.getRutPaciente());
        assertEquals(30, dto.getEdad());
    }
}