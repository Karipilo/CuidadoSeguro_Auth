package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;
import com.hospital.authservice.entity.Paciente;
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
public class PacienteUser implements User {
    
    private final RoleRepository roleRepository;
    
    @Override
    public Usuario crearUsuario(RegisterRequest request) {
        log.info("Creando usuario de tipo PACIENTE para: {}", request.getUsername());
        
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
        
        Paciente paciente = Paciente.builder()
                .historiaClinica("HC-" + System.currentTimeMillis())
                .grupoSanguineo(request.getGrupoSanguineo())
                .factorRh(request.getFactorRh())
                .alergias(request.getAlergias())
                .enfermedadesCronicas(request.getEnfermedadesCronicas())
                .medicamentosActuales(request.getMedicamentosActuales())
                .contactoEmergencia(request.getContactoEmergencia())
                .telefonoEmergencia(request.getTelefonoEmergencia())
                .prevision(request.getPrevision())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .usuario(usuario)
                .build();
        
        persona.setUsuario(usuario);
        usuario.setPaciente(paciente);
        
        return usuario;
    }
    
    @Override
    public void validarDatosEspecificos(RegisterRequest request) {
        // Validaciones específicas para pacientes
        
        
        boolean tieneRolePaciente = request.getRoles().stream()
                .anyMatch(role -> role.equals("ROLE_PACIENTE"));
        
        if (!tieneRolePaciente) {
            throw new IllegalArgumentException("El usuario paciente debe tener el rol ROLE_PACIENTE");
        }
        
        log.debug("Validaciones específicas de PACIENTE pasadas para usuario: {}", request.getUsername());
    }
    
    @Override
    public String getTipoUsuario() {
        return "PACIENTE";
    }
}
