package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;
import com.hospital.authservice.entity.Tutor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFactory {

    private final AdminUser adminUser;
    private final MedicoUser medicoUser;
    private final PacienteUser pacienteUser;
    private final TutorUser tutorUser;

    private final Map<String, User> userCreators = new HashMap<>();

    @PostConstruct
    public void init() {
        userCreators.put("ADMIN", adminUser);
        userCreators.put("MEDICO", medicoUser);
        userCreators.put("PACIENTE", pacienteUser);
        userCreators.put("TUTOR", tutorUser);
    }

    public User createUser(String tipoUsuario) {
        if (tipoUsuario == null) {
            throw new IllegalArgumentException("Tipo de usuario no puede ser null");
        }

        User creator = userCreators.get(tipoUsuario.toUpperCase());

        if (creator == null) {
            throw new IllegalArgumentException("Tipo de usuario no soportado: " + tipoUsuario);
        }

        return creator;
    }

    public boolean isSupportedUserType(String tipoUsuario) {
        return tipoUsuario != null && userCreators.containsKey(tipoUsuario.toUpperCase());
    }

    public Set<String> getSupportedUserTypes() {
        return userCreators.keySet();
    }
}