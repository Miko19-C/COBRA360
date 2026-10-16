param([string]$BaseUrl = 'http://127.0.0.1:8080')
$ErrorActionPreference = 'Stop'
$state = Invoke-RestMethod "$BaseUrl/api/session" -SessionVariable cobraWeb
$headers = @{}
$headers[$state.csrfHeader] = $state.csrf
$null = Invoke-RestMethod "$BaseUrl/api/login" -Method Post -WebSession $cobraWeb -Headers $headers -Body @{username='supervisor';password='Cobra360!2026'}
$state = Invoke-RestMethod "$BaseUrl/api/session" -WebSession $cobraWeb
$headers[$state.csrfHeader] = $state.csrf
if (-not $state.authenticated) { throw 'No se pudo autenticar al supervisor demo.' }
foreach ($route in @('cartera','promesas','pagos','resumen','reportes','catalogos','patrones','migraciones','auditoria','plantillas/gestion')) {
    $result = Invoke-RestMethod "$BaseUrl/api/$route" -WebSession $cobraWeb
    if ($null -eq $result) { throw "Respuesta nula en $route" }
    Write-Output "OK GET /api/$route"
}
$patterns = Invoke-RestMethod "$BaseUrl/api/patrones" -WebSession $cobraWeb
if ($patterns.Count -ne 9) { throw 'No se encontraron los nueve patrones.' }
$migrations = Invoke-RestMethod "$BaseUrl/api/migraciones" -WebSession $cobraWeb
if ($migrations.Count -ne 3) { throw 'No se encontraron las tres migraciones.' }
$null = Invoke-RestMethod "$BaseUrl/api/logout" -Method Post -WebSession $cobraWeb -Headers $headers
Write-Output 'Verificación HTTP completada; no se modificaron datos del negocio.'
