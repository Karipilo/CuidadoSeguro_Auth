package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;
import com.hospital.authservice.entity.Persona;
import com.hospital.authservice.entity.Role;
import com.hospital.authservice.entity.Usuario;
import com.hospital.authservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUser implements User {
    
    private final RoleRepository roleRepository;
    
    @Override
    public Usuario crearUsuario(RegisterRequest request) {
        log.info("Creando usuario de tipo ADMIN para: {}", request.getUsername());
        
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
        
        persona.setUsuario(usuario);
        
        return usuario;
    }
    
    @Override
    public void validarDatosEspecificos(RegisterRequest request) {
        // Validaciones específicas para usuarios administradores
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            throw new IllegalArgumentException("Los roles son obligatorios para usuarios administradores");
        }
        
        boolean tieneRoleAdmin = request.getRoles().stream()
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        
        if (!tieneRoleAdmin) {
            throw new IllegalArgumentException("El usuario administrador debe tener el rol ROLE_ADMIN");
        }
        
        log.debug("Validaciones específicas de ADMIN pasadas para usuario: {}", request.getUsername());
    }
    
    @Override
    public String getTipoUsuario() {
        return "ADMIN";
    }
}
