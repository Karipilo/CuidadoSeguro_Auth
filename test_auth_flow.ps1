# Script para probar flujo completo de autenticación

$BASE_URL = "http://localhost:8081"
$HEADERS = @{
    "Content-Type" = "application/json"
}

Write-Host "=== TEST AUTENTICACIÓN ===" -ForegroundColor Cyan
Write-Host ""

# 1. Realizar Health Check
Write-Host "1️⃣  Health Check..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "$BASE_URL/auth/health" -Method Get -Headers $HEADERS
    Write-Host "✅ Servicio disponible: $($healthResponse.data)" -ForegroundColor Green
} catch {
    Write-Host "❌ Error en health check: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. Registrar nuevo usuario
Write-Host "2️⃣  Registrando nuevo usuario..." -ForegroundColor Yellow

$registerBody = @{
    username = "TestUser123"
    password = "SecurePass123!"
    email = "testuser@hospital.com"
    numeroDocumento = "12345678"
    tipoDocumento = "DNI"
    nombres = "Juan"
    apellidos = "Pérez"
    tipoUsuario = "PACIENTE"
    aceptaTerminos = $true
    versionTerminos = 1
    genero = "M"
    fechaNacimiento = "1990-01-15"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$BASE_URL/auth/register" `
        -Method Post `
        -Headers $HEADERS `
        -Body $registerBody

    Write-Host "✅ Usuario registrado exitosamente" -ForegroundColor Green
    Write-Host "   Access Token: $($registerResponse.accessToken.Substring(0, 20))..." -ForegroundColor Gray
} catch {
    Write-Host "❌ Error en registro: $($_)" -ForegroundColor Red
    Write-Host $_.Exception.Response.Content -ForegroundColor Red
    exit 1
}

Write-Host ""

# 3. Intentar login con las mismas credenciales
Write-Host "3️⃣  Intentando login con credenciales..." -ForegroundColor Yellow

$loginBody = @{
    username = "TestUser123"
    password = "SecurePass123!"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$BASE_URL/auth/login" `
        -Method Post `
        -Headers $HEADERS `
        -Body $loginBody

    Write-Host "✅ LOGIN EXITOSO!" -ForegroundColor Green
    Write-Host "   Usuario: $($loginResponse.userInfo.username)" -ForegroundColor Cyan
    Write-Host "   Email: $($loginResponse.userInfo.email)" -ForegroundColor Cyan
    Write-Host "   Tipo: $($loginResponse.userInfo.tipoUsuario)" -ForegroundColor Cyan
    Write-Host "   Token válido por: $($loginResponse.expiresIn) segundos" -ForegroundColor Cyan
    
} catch {
    Write-Host "❌ Error en login: $($_)" -ForegroundColor Red
    
    # Intentar extraer más detalles del error
    try {
        $errorContent = $_.Exception.Response | ForEach-Object { $_.Content | Out-String }
        Write-Host "Detalles: $errorContent" -ForegroundColor Red
    } catch {}
    
    exit 1
}

Write-Host ""
Write-Host "✅ PRUEBA COMPLETADA EXITOSAMENTE" -ForegroundColor Green
