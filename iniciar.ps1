$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
if (Test-Path -LiteralPath '.env') {
    foreach ($line in Get-Content -LiteralPath '.env') {
        if ($line -match '^([A-Z_]+)=(.*)$') { [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process') }
    }
}
docker compose up -d --wait
if ($LASTEXITCODE -ne 0) { throw 'No se pudo iniciar MySQL. Abre Docker Desktop y vuelve a intentar.' }
if (-not $env:SPRING_PROFILES_ACTIVE) { $env:SPRING_PROFILES_ACTIVE = 'demo' }
& (Join-Path $PSScriptRoot 'mvnw.cmd') -pl backend spring-boot:run
if ($LASTEXITCODE -ne 0) { throw 'Spring Boot no pudo arrancar. Revisa el error anterior.' }
