package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;
import com.hospital.authservice.entity.Profesional;
import com.hospital.authservice.entity.Persona;
import com.hospital.authservice.entity.Role;
import com.hospital.authservice.entity.Usuario;
import com.hospital.authservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfesionalUser implements User {
    
    private final RoleRepository roleRepository;
    
    @Override
    public Usuario crearUsuario(RegisterRequest request) {
        log.info("Creando usuario de tipo PROFESIONAL para: {}", request.getUsername());
        
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
        
        Profesional profesional = Profesional.builder()
                .numeroLicencia(request.getNumeroLicencia())
                .profesion(request.getProfesion())
                .especialidad(request.getEspecialidad())
                .subespecialidad(request.getSubespecialidad())
                .universidad(request.getUniversidad())
                .anioGraduacion(request.getAnioGraduacion())
                .experienciaAnios(request.getExperienciaAnios() != null ? request.getExperienciaAnios() : 0)
                .institucion(request.getInstitucion())
                .horasSemanales(request.getHorasSemanales())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .usuario(usuario)
                .build();
        
        persona.setUsuario(usuario);
        usuario.setProfesional(profesional);
        
        return usuario;
    }
    
    @Override
    public void validarDatosEspecificos(RegisterRequest request) {
        // Validaciones específicas para médicos
        if (request.getProfesion() == null || request.getProfesion().trim().isEmpty()) {
            throw new IllegalArgumentException("La profesión es obligatoria para profesionales");
        }
        
        if (request.getNumeroLicencia() == null || request.getNumeroLicencia().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de licencia es obligatorio para médicos");
        }
        
        if (request.getEspecialidad() == null || request.getEspecialidad().trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad es obligatoria para médicos");
        }
        
        if (request.getUniversidad() == null || request.getUniversidad().trim().isEmpty()) {
            throw new IllegalArgumentException("La universidad de graduación es obligatoria para médicos");
        }
        
        if (request.getAnioGraduacion() == null || request.getAnioGraduacion() < 1950 || request.getAnioGraduacion() > 2026) {
            throw new IllegalArgumentException("El año de graduación no es válido");
        }
        
        boolean tieneRoleProfesional = request.getRoles().stream()
                .anyMatch(role -> role.equals("ROLE_PROFESIONAL"));
        
        if (!tieneRoleProfesional) {
            throw new IllegalArgumentException("El usuario médico debe tener el rol ROLE_PROFESIONAL");
        }
        
        log.debug("Validaciones específicas de PROFESIONAL pasadas para usuario: {}", request.getUsername());
    }
    
    @Override
    public String getTipoUsuario() {
        return "PROFESIONAL";
    }
}
