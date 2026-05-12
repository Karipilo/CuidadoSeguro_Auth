package com.hospital.authservice.service;

import com.hospital.authservice.dto.*;
import com.hospital.authservice.entity.*;
import com.hospital.authservice.exception.AuthException;
import com.hospital.authservice.factory.UserFactory;
import com.hospital.authservice.repository.*;
import com.hospital.authservice.security.JwtService;
import com.hospital.authservice.utils.CryptoUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final RoleRepository roleRepository;
    private final PersonaRepository personaRepository;
    private final ProfesionalRepository profesionalRepository;
    private final PacienteRepository pacienteRepository;
    private final TutorRepository tutorRepository;
    private final UserFactory userFactory;
    private final ConsentimientoRepository consentimientoRepository;
    private final TerminosCondicionesRepository terminosCondicionesRepository;
    

    @Transactional(readOnly = true)
public Paciente obtenerDetallesUsuario(String token) {

    try {
        token = token.replace("Bearer ", "").trim();

        if (token.isEmpty()) {
            throw new AuthException("Token no proporcionado");
        }

        if (!validateToken(token)) {
            throw new AuthException("Token inválido o revocado");
        }

        // 1. Extraer userId desde JWT
        Long userId = jwtService.getUserIdFromToken(token);

        if (userId == null) {
            throw new AuthException("Token sin userId");
        }

        log.info("UserId desde JWT: {}", userId);

        // 2. Buscar usuario por ID
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado en BD. ID usado: {}", userId);
                    return new AuthException("Usuario no encontrado");
                });

        

        return usuario.getPaciente();

    } catch (AuthException e) {
        throw e;
    } catch (Exception e) {
        log.error("Error obteniendo detalles de usuario", e);
        throw new AuthException("Error procesando token");
    }
}

    @Transactional
    public boolean validateToken(String token) {
        try {
            // Verifica si el token está en blacklist
            boolean isBlacklisted = tokenBlacklistRepository.existsByToken(token);
            if (isBlacklisted) {
                return false;
            }

            // Validar token con JWT
            return jwtService.isTokenValid(token);

        } catch (Exception e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            Usuario usuario = (Usuario) authentication.getPrincipal();

            // Actualizar último login
            usuarioRepository.actualizarUltimoLogin(usuario.getId(), LocalDateTime.now());

            // Generar tokens
            Map<String, Object> accessClaims = new HashMap<>();
            Map<String, Object> refreshClaims = new HashMap<>();

            String accessToken = jwtService.generateToken(
                    accessClaims,
                    usuario
            );

            String refreshToken = jwtService.generateRefreshToken(
                    refreshClaims,
                    usuario,
                    usuario.getEmail()
            );

            // Guardar refresh token en BD
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .token(refreshToken)
                    .usuario(usuario)
                    .fechaExpiracion(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpiration()))
                    .build();

            refreshTokenRepository.save(refreshTokenEntity);

            // Revocar tokens anteriores
            refreshTokenRepository.revocarTodosTokensUsuario(usuario);

            String username;
            String email;

            try {
                username = CryptoUtil.decrypt(usuario.getUsername());
                email = CryptoUtil.decrypt(usuario.getEmail());
            } catch (Exception e) {
                log.warn("Error desencriptando datos: {}", e.getMessage());
                username = usuario.getUsername();
                email = usuario.getEmail();
            }

            // Construir response
            AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                    .id(usuario.getId())
                    .username(username)
                    .email(email)
                    .nombreCompleto(usuario.getPersona().getNombres() + " " + usuario.getPersona().getApellidos())
                    .tipoUsuario(determinarTipoUsuario(usuario))
                    .roles(usuario.getAuthorities().stream()
                            .map(auth -> auth.getAuthority())
                            .toList())
                    .build();

            AuthResponse response = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getJwtExpiration())
                    .userInfo(userInfo)
                    .message("Login exitoso")
                    .build();

            log.info("Login exitoso para usuario: {}", request.getUsername());
            return response;

        } catch (BadCredentialsException e) {
            /*
            Optional<Usuario> usuarioOpt;
            try {
                String encryptedUsername = CryptoUtil.encrypt(request.getUsername());
                usuarioOpt = usuarioRepository.findByUsername(encryptedUsername);
            } catch (Exception ex) {
                usuarioOpt = Optional.empty();
            }
            */

            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(
            CryptoUtil.encrypt(request.getUsername()));

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                int intentos = usuario.getIntentosFallidos() + 1;

                usuarioRepository.actualizarIntentosFallidos(usuario.getId(), intentos);

                if (intentos >= 5) {
                    usuarioRepository.bloquearUsuario(usuario.getId());
                    log.warn("Usuario bloqueado por exceder intentos fallidos: {}", request.getUsername());
                }
            }

            log.warn("Credenciales inválidas para usuario: {}", request.getUsername());
            throw new AuthException("Credenciales inválidas");
        }
    }

    public static boolean tieneCaracterEspecial(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        // Coincide si hay al menos un carácter que no sea letra ni número
        return !s.matches("[A-Za-z0-9 ]*"); // El espacio está permitido aquí
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Intento de registro para usuario: {}", request.getUsername());
        
        // Validaciones básicas
        String encryptedUsername;
String encryptedEmail;

    try {

        encryptedUsername = CryptoUtil.encrypt(request.getUsername());
        encryptedEmail = CryptoUtil.encrypt(request.getEmail());

    } catch (Exception e) {

        throw new AuthException("Error encriptando datos");

    }

    if (usuarioRepository.existsByUsername(encryptedUsername)) {
        throw new AuthException("El username ya está en uso");
    }

    if (usuarioRepository.existsByEmail(encryptedEmail)) {
        throw new AuthException("El email ya está en uso");
    }
        
        if (personaRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new AuthException("El número de documento ya está registrado");
        }
        
        // Validar tipo de usuario
        if (!userFactory.isSupportedUserType(request.getTipoUsuario())) {
            throw new AuthException("Tipo de usuario no soportado: " + request.getTipoUsuario());
        }
        
        // Validar términos y condiciones
        if (request.getAceptaTerminos() == null || !request.getAceptaTerminos()) {
            throw new AuthException("Debe aceptar los términos y condiciones");
        }
        
        // Crear usuario usando factory
        com.hospital.authservice.factory.User userCreator = userFactory.createUser(request.getTipoUsuario());
        Usuario usuario = userCreator.crearUsuario(request);
        
        usuario.setUsername(encryptedUsername);
        usuario.setEmail(encryptedEmail);
        if (request.getPassword().chars().anyMatch(Character::isLowerCase) &&
            request.getPassword().chars().anyMatch(Character::isUpperCase) &&
            request.getPassword().chars().anyMatch(Character::isDigit) &&
            tieneCaracterEspecial(request.getPassword())){

                // Encriptar contraseña
                usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        
        }else{
            String ret = "La contraseña debe tener al menos: ";

            if (!request.getPassword().chars().anyMatch(Character::isLowerCase)){
                ret = ret + "1 letra minúscula";
            }


            if (!request.getPassword().chars().anyMatch(Character::isUpperCase)){
                if (ret.contains("1")){
                    ret = ret + ",";
                }
                ret = ret + "1 letra mayúscula";
            }

            

            if (!request.getPassword().chars().anyMatch(Character::isDigit)){
                if (ret.contains("1")){
                    ret = ret + ",";
                }
                ret = ret + "1 número";
            }

            if (!tieneCaracterEspecial(request.getPassword().toString())){
                if (ret.contains("1")){
                    ret = ret + ",";
                }
                ret = ret + "1 carácter especial";
            }

            
            ret = ret + ".";
            

            throw new AuthException(ret);
        }
        
        if (request.getPassword().toString().length()<8){
            throw new AuthException("La contraseña debe tener al menos 8 carácteres.");
        }

        if (request.getPassword().toString().length()>100){
            throw new AuthException("La contraseña debe tener menos de 101 carácteres, ahora tiene: "+request.getPassword().toString()+" carácteres.");
        }

        if ("TUTOR".equals(request.getTipoUsuario())) {
            String ret = "";
            for (int i =0; i<request.getPacientesRuts().size();i++){
                if (!(personaRepository.existsByNumeroDocumento(request.getPacientesRuts().get(i)))){
                    ret = ret + "Persona ("+(i+1)+") No existe en los registros \r\n";
                    i = request.getPacientesRuts().size();
                }
                
            }

            if (!ret.equals("")){
                throw new AuthException(ret);
            }else{
                //tutorRepository.save(null)
            }
            
        }
        
        // Validaciones específicas según tipo
        if ("PROFESIONAL".equals(request.getTipoUsuario())) {
            if (profesionalRepository.existsByNumeroLicencia(request.getNumeroLicencia())) {
                throw new AuthException("El número de licencia ya está registrada");
            }
        }
        
        if ("PACIENTE".equals(request.getTipoUsuario())
                && request.getHistoriaClinica() != null
                && pacienteRepository.existsByHistoriaClinica(request.getHistoriaClinica())) {
            
            throw new AuthException("La historia clínica ya está registrada");
        }
        

        // Guardar entidades
        usuarioRepository.save(usuario);
        
        // Guardar consentimiento de términos y condiciones
        if (request.getVersionTerminos() != null) {
            Optional<TerminosCondiciones> terminosOpt = terminosCondicionesRepository
                    .findByVersion(request.getVersionTerminos());
            if (terminosOpt.isPresent()) {
                Consentimiento consentimiento = Consentimiento.builder()
                        .usuario(usuario)
                        .terminosCondiciones(terminosOpt.get())
                        .aceptado(true)
                        .build();
                consentimientoRepository.save(consentimiento);
            }
        }
        
        // Generar tokens para login automático
        /*
        //Forma antigua y estable:
        String accessToken = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);
        */

        Map<String, Object> accessClaims = new HashMap<>();
        Map<String, Object> refreshClaims = new HashMap<>();

        String accessToken = jwtService.generateToken(
                accessClaims,
                usuario
        );

        String refreshToken = jwtService.generateRefreshToken(
                refreshClaims,
                usuario,
                usuario.getEmail()
        );
        // Guardar refresh token
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .usuario(usuario)
                .fechaExpiracion(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpiration()))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);
        
        // Construir response
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(usuario.getId())
                .username(CryptoUtil.decrypt(usuario.getUsername()))
                .email(CryptoUtil.decrypt(usuario.getEmail()))
                .nombreCompleto(usuario.getPersona().getNombres() + " " + usuario.getPersona().getApellidos())
                .tipoUsuario(request.getTipoUsuario())
                .roles(usuario.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .toList())
                .build();
        
        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpiration())
                .userInfo(userInfo)
                .message("Registro exitoso")
                .build();
        
        log.info("Registro exitoso para usuario: {}", request.getUsername());
        return response;
    }
    
    @Transactional
    public AuthResponse refreshToken(RefreshRequest request) {
        log.debug("Intento de refresh token");
        
        String refreshToken = request.getRefreshToken();
        
        // Validar refresh token
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(refreshToken);
        if (refreshTokenOpt.isEmpty()) {
            throw new AuthException("Refresh token no encontrado");
        }
        
        RefreshToken refreshTokenEntity = refreshTokenOpt.get();
        
        // Validar que no esté revocado ni usado ni expirado
        if (refreshTokenEntity.getRevocado() || refreshTokenEntity.getUsado() || refreshTokenEntity.isExpirado()) {
            throw new AuthException("Refresh token inválido");
        }
        
        // Obtener usuario
        Usuario usuario = refreshTokenEntity.getUsuario();
        
        // Marcar refresh token actual como usado
        refreshTokenEntity.marcarComoUsado();
        refreshTokenRepository.save(refreshTokenEntity);
        
        // Generar nuevos tokens
        
        Map<String, Object> accessClaims = new HashMap<>();
        Map<String, Object> refreshClaims = new HashMap<>();

        String newAccessToken = jwtService.generateToken(
                accessClaims,
                usuario
        );
        
        String newRefreshToken = jwtService.generateRefreshToken(
                refreshClaims,
                usuario,
                usuario.getEmail()
        );
        
        // Guardar nuevo refresh token
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .usuario(usuario)
                .fechaExpiracion(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpiration()))
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);
        
        // Construir response
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getPersona().getNombres() + " " + usuario.getPersona().getApellidos())
                .tipoUsuario(determinarTipoUsuario(usuario))
                .roles(usuario.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .toList())
                .build();
        
        AuthResponse response = AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpiration())
                .userInfo(userInfo)
                .message("Token refrescado exitosamente")
                .build();
        
        log.debug("Refresh token exitoso para usuario: {}", usuario.getUsername());
        return response;
    }
    
    @Transactional
    public ApiResponseDto<Void> logout(LogoutRequest request) {
        log.info("Intento de logout");
        
        String accessToken = request.getAccessToken();
        String refreshToken = request.getRefreshToken();
        
        // Agregar access token a blacklist
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                LocalDateTime expiracion = jwtService.getExpirationDate(accessToken);
                
                TokenBlacklist blacklistToken = TokenBlacklist.builder()
                        .token(accessToken)
                        .fechaExpiracion(expiracion)
                        .motivo("Logout")
                        .build();
                
                tokenBlacklistRepository.save(blacklistToken);
                log.debug("Access token agregado a blacklist");
                
            } catch (Exception e) {
                log.warn("No se pudo procesar el access token para blacklist: {}", e.getMessage());
            }
        }
        
        // Revocar refresh token
        if (refreshToken != null && !refreshToken.isEmpty()) {
            Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(refreshToken);
            if (refreshTokenOpt.isPresent()) {
                RefreshToken refreshTokenEntity = refreshTokenOpt.get();
                refreshTokenEntity.revocar();
                refreshTokenRepository.save(refreshTokenEntity);
                log.debug("Refresh token revocado");
            }
        }
        
        log.info("Logout exitoso");
        return ApiResponseDto.success(null, "Logout exitoso");
    }
    
    @Transactional
    public void limpiarTokensExpirados() {
        log.debug("Limpiando tokens expirados");
        
        LocalDateTime ahora = LocalDateTime.now();
        
        // Limpiar refresh tokens expirados
        refreshTokenRepository.eliminarTokensExpirados(ahora);
        
        // Limpiar tokens de blacklist expirados
        tokenBlacklistRepository.eliminarTokensExpirados(ahora);
        
        log.debug("Limpieza de tokens expirados completada");
    }
    
    private String determinarTipoUsuario(Usuario usuario) {
        if (usuario.getProfesional() != null) {
            return "PROFESIONAL";
        } else if (usuario.getPaciente() != null) {
            return "PACIENTE";
        } else if (usuario.getTutor() != null) {
            return "TUTOR";
        }else{
            return "ADMIN";
        }
    }
}
