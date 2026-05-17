$BASE_URL = "http://localhost:8081"
$HEADERS = @{ "Content-Type" = "application/json" }

Write-Host "TEST AUTENTICACION" -ForegroundColor Cyan

# 1. Health Check
Write-Host "1. Verificando servicio..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "$BASE_URL/auth/health" -Method Get
    Write-Host "[OK] Servicio disponible" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Servicio no disponible: $_" -ForegroundColor Red
    exit 1
}

# 2. Registro
Write-Host "2. Registrando usuario..." -ForegroundColor Yellow
$registerData = @{
    username = "TestUser999"
    password = "SecurePass123!"
    email = "testuser999@hospital.com"
    numeroDocumento = "12345999"
    tipoDocumento = "DNI"
    nombres = "Juan"
    apellidos = "Perez"
    tipoUsuario = "PACIENTE"
    aceptaTerminos = $true
    versionTerminos = 1
    genero = "M"
    fechaNacimiento = "1990-01-15"
} | ConvertTo-Json

try {
    $regResp = Invoke-RestMethod -Uri "$BASE_URL/auth/register" -Method Post -Headers $HEADERS -Body $registerData
    Write-Host "[OK] Usuario registrado" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Registro fallido: $_" -ForegroundColor Red
}

# 3. Login
Write-Host "3. Intentando login..." -ForegroundColor Yellow
$loginData = @{
    username = "TestUser999"
    password = "SecurePass123!"
} | ConvertTo-Json

try {
    $loginResp = Invoke-RestMethod -Uri "$BASE_URL/auth/login" -Method Post -Headers $HEADERS -Body $loginData
    Write-Host "[OK] LOGIN EXITOSO!!!" -ForegroundColor Green
    Write-Host "Usuario: $($loginResp.userInfo.username)" -ForegroundColor Cyan
    Write-Host "Email: $($loginResp.userInfo.email)" -ForegroundColor Cyan
} catch {
    Write-Host "[ERROR] Login fallido" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}

Write-Host ""
Write-Host "TEST COMPLETADO" -ForegroundColor Green
