$BASE_URL = "http://localhost:8081"
$HEADERS = @{ "Content-Type" = "application/json" }

Write-Host "=== TEST DETALLADO ===" -ForegroundColor Cyan

$registerData = @{
    username = "TestUser888"
    password = "SecurePass123!"
    email = "testuser888@hospital.com"
    numeroDocumento = "12345888"
    tipoDocumento = "DNI"
    nombres = "Juan"
    apellidos = "Perez"
    tipoUsuario = "PACIENTE"
    aceptaTerminos = $true
    versionTerminos = 1
    genero = "M"
    fechaNacimiento = "1990-01-15"
} | ConvertTo-Json

Write-Host "JSON enviado:"
Write-Host $registerData
Write-Host ""

try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/auth/register" -Method Post -Headers $HEADERS -Body $registerData -ErrorVariable ResponseError
    Write-Host "Respuesta: $response"
} catch {
    Write-Host "ERROR CAPTURADO:" -ForegroundColor Red
    Write-Host "Mensaje: $($_.Exception.Message)"
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $reader.DiscardBufferedData()
        $content = $reader.ReadToEnd()
        Write-Host "Respuesta del servidor:" -ForegroundColor Yellow
        Write-Host $content -ForegroundColor Yellow
    }
}
