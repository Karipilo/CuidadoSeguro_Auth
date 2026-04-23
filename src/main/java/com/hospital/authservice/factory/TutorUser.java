package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;
import com.hospital.authservice.entity.*;
import com.hospital.authservice.repository.PacienteRepository;
import com.hospital.authservice.repository.RoleRepository;
import com.hospital.authservice.repository.TutorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TutorUser implements User {

    private final RoleRepository roleRepository;
    private final PacienteRepository pacienteRepository;
    private final TutorRepository tutorRepository;

    @Override
    public Usuario crearUsuario(RegisterRequest request) {

        log.info("Creando usuario de tipo TUTOR para: {}", request.getUsername());

        validarDatosEspecificos(request);

        
        Persona persona = Persona.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .fechaNacimiento(request.getFechaNacimiento())
                .genero(request.getGenero())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .direccion(request.getDireccion())
                .activo(true)
                .build();

        
        Set<Role> roles = new HashSet<>();
        request.getRoles().forEach(roleName -> {
            Role role = roleRepository.findByNombre(roleName)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
            roles.add(role);
        });

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .activo(true)
                .noBloqueado(true)
                .persona(persona)
                .roles(roles)
                .build();

        
        List<String> pacientes = request.getPacientesRuts();

        if (pacientes.isEmpty()) {
            throw new RuntimeException("No se encontraron pacientes con los RUTs proporcionados");
        }

        Tutor tutor = Tutor.builder()
                .usuario(usuario)
                .parentezco(request.getParentezco())
                .pacientes(pacientes.toString())
                .build();

        persona.setUsuario(usuario);
        usuario.setTutor(tutor);

        return usuario;
    }

    @Override
    public void validarDatosEspecificos(RegisterRequest request) {

        if (request.getParentezco() == null) {
            throw new IllegalArgumentException("El parentezco es obligatorio");
        }

        if (request.getPacientesRuts() == null || request.getPacientesRuts().isEmpty()) {
            throw new IllegalArgumentException("Debe asociar al menos un paciente");
        }

        boolean tieneRoleTutor = request.getRoles().stream()
                .anyMatch(role -> role.equals("ROLE_TUTOR"));

        if (!tieneRoleTutor) {
            throw new IllegalArgumentException("Debe incluir ROLE_TUTOR");
        }

        log.debug("Validaciones de TUTOR correctas para: {}", request.getUsername());
    }

    @Override
    public String getTipoUsuario() {
        return "TUTOR";
    }
}