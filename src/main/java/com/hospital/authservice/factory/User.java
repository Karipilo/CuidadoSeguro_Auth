package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;
import com.hospital.authservice.entity.Usuario;

public interface User {
    
    Usuario crearUsuario(RegisterRequest request);
    
    void validarDatosEspecificos(RegisterRequest request);
    
    String getTipoUsuario();
}
