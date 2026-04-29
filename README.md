# Auth Service - Microservicio de Autenticación

Microservicio de autenticación completo con JWT, refresh tokens y blacklist para el sistema hospitalario.

## Características Principales

### Autenticación y Autorización
- **JWT Access Token**: Corta duración (15 min por defecto)
- **Refresh Token**: Larga duración (7 días por defecto) con rotación automática
- **Blacklist**: Logout real invalidando tokens en base de datos
- **Seguridad avanzada**: BCrypt, validación contra blacklist, intentos fallidos

### Patrones de Diseño
- **Factory Method**: Creación de usuarios según tipo (Admin, Médico, Paciente)
- **Repository Pattern**: Abstracción de acceso a datos
- **Circuit Breaker**: Resilience4j para tolerancia a fallos

### Arquitectura
- **Capas**: Controller, Service, Repository, Config, Security, DTOs
- **Base de datos**: MySQL con JPA/Hibernate
- **Testing**: Unitarios (JUnit + Mockito) e Integración (Testcontainers)

## Stack Tecnológico

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Security**
- **Spring Data JPA**
- **MySQL 8.0**
- **JWT (JJWT)**
- **Resilience4j**
- **Swagger/OpenAPI 3**
- **Testcontainers**
- **Docker**
- **GitHub Actions**

## Endpoints

### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/refresh` - Refrescar token
- `POST /api/auth/logout` - Cerrar sesión

### Utilitarios
- `GET /api/auth/validate` - Validar token
- `GET /api/auth/health` - Health check

## Tipos de Usuario

### Administrador
- Acceso completo al sistema
- Roles: `ROLE_ADMIN`

### Profesional
- Información profesional: licencia, especialidad, universidad
- Roles: `ROLE_PROFESIONAL`

### Tutor:
- Rol de tutor de un paciente.
- Roles: `ROLE_TUTOR`

### Paciente
- Acceso a sus datos médicos
- Información personal: historia clínica, alergias, seguros
- Roles: `ROLE_PACIENTE`

## Configuración

### Base de Datos
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_service
    username: auth_user
    password: auth_password
```

### JWT
```yaml
jwt:
  secret: miClaveSecretaMuyLargaYSeguraParaJWT123456789
  expiration: 900  # 15 minutos
  refresh-expiration: 604800  # 7 días
```

## Docker

### Construir imagen
```bash
docker build -t auth-service .
```

### Ejecutar con docker-compose
```bash
# Desarrollo
docker-compose up

# Producción
docker-compose --profile production up

# Con monitoreo
docker-compose --profile monitoring up
```

## Testing

### Tests Unitarios
```bash
mvn test
```

### Tests de Integración
```bash
mvn test -Dtest=**/*IntegrationTest
```

### Cobertura
```bash
mvn jacoco:report
```

## API Documentation

La documentación está disponible en:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Flujo de Autenticación

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez",
    "password": "Password123!"
  }'
```

### 2. Registro
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo.usuario",
    "password": "Password123!",
    "email": "nuevo@hospital.com",
    "tipoUsuario": "PACIENTE",
    "nombres": "Juan",
    "apellidos": "Perez",
    "tipoDocumento": "DNI",
    "numeroDocumento": "12345678",
    "historiaClinica": "HC-001234",
    "aceptaTerminos": true,
    "roles": ["ROLE_PACIENTE"]
  }'
```

### 3. Refresh Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

### 4. Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

## Seguridad

### Características de Seguridad
- Contraseñas encriptadas con BCrypt
- Tokens JWT firmados con clave secreta
- Blacklist para invalidar tokens
- Validación de fuerza de contraseñas
- Límite de intentos fallidos (5 intentos)
- Bloqueo automático de cuentas
- CORS configurado

### Headers de Autenticación
```bash
Authorization: Bearer <access_token>
```

## Monitoring

### Health Checks
- Actuator: `/actuator/health`
- Custom: `/api/auth/health`

### Métricas
- Circuit Breaker status
- Database connection pool
- JVM metrics

## Development

### Estructura del Proyecto
```
src/
  main/
    java/com/hospital/authservice/
      entity/          # Entidades JPA
      repository/      # Repositorios Spring Data
      service/         # Lógica de negocio
      controller/      # Endpoints REST
      security/        # Configuración de seguridad
      config/          # Configuración general
      factory/         # Factory pattern
      exception/       # Excepciones personalizadas
      dto/            # Data Transfer Objects
  test/
    java/com/hospital/authservice/
      service/        # Tests unitarios
      controller/     # Tests de controladores
      integration/    # Tests de integración
```

### Perfiles
- `dev` - Desarrollo con H2
- `prod` - Producción con MySQL
- `test` - Tests con Testcontainers

## CI/CD

### GitHub Actions
- **Build**: Compilación y tests
- **Security**: Análisis con Trivy
- **Quality**: SonarCloud analysis
- **Docker**: Build y push de imágenes
- **Deploy**: Despliegue automático en releases

### Pipeline Stages
1. Checkout código
2. Setup Java 17
3. Build con Maven
4. Ejecutar tests (unitarios + integración)
5. Análisis de calidad (SonarCloud)
6. Build Docker image
7. Security scan (Trivy)
8. Deploy (solo en releases)

## Performance

### Optimizaciones
- Connection pooling con HikariCP
- Batch operations en JPA
- Caching con Redis (opcional)
- Lazy loading en relaciones
- Índices en base de datos

### Métricas de Rendimiento
- Tiempo de respuesta: < 200ms
- Throughput: > 1000 req/s
- Memory usage: < 512MB
- CPU usage: < 50%

## Troubleshooting

### Issues Comunes
1. **Token expirado**: Usar refresh token
2. **Usuario bloqueado**: Contactar administrador
3. **Conexión BD**: Verificar configuración
4. **CORS**: Configurar orígenes permitidos

### Logs
- Application logs: `logs/auth-service.log`
- Access logs: Configurable en aplicación
- Error logs: Nivel ERROR por defecto

## Contribución

1. Fork del proyecto
2. Feature branch: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -m 'Add nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Pull Request

## Licencia

Apache License 2.0
