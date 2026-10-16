param([string]$BaseUrl = 'http://127.0.0.1:5173')
$ErrorActionPreference = 'Stop'
foreach ($demoUser in @('supervisor', 'mirko', 'lucia', 'diego')) {
    $session = Invoke-RestMethod "$BaseUrl/api/session" -SessionVariable roleWeb
    $headers = @{}
    $headers[$session.csrfHeader] = $session.csrf
    $null = Invoke-RestMethod "$BaseUrl/api/login" -Method Post -WebSession $roleWeb -Headers $headers -Body @{username=$demoUser;password='Cobra360!2026'}
    $session = Invoke-RestMethod "$BaseUrl/api/session" -WebSession $roleWeb
    if (-not $session.authenticated -or $session.user.usuario -ne $demoUser) { throw "Sesión incorrecta: $demoUser" }
    foreach ($route in @('cartera', 'promesas', 'pagos', 'resumen', 'reportes', 'catalogos')) {
        $result = Invoke-RestMethod "$BaseUrl/api/$route" -WebSession $roleWeb
        if ($demoUser -ne 'supervisor' -and $route -in @('cartera', 'promesas')) {
            foreach ($row in $result) {
                if ($row.id_asesor -ne $session.user.asesorId) { throw "Datos de otro asesor en $route para $demoUser" }
            }
        }
    }
    if ($demoUser -ne 'supervisor') {
        $denied = Invoke-WebRequest "$BaseUrl/api/auditoria" -WebSession $roleWeb -SkipHttpErrorCheck
        if ($denied.StatusCode -ne 403) { throw "Acceso técnico no bloqueado para $demoUser" }
    }
    $headers[$session.csrfHeader] = $session.csrf
    $null = Invoke-RestMethod "$BaseUrl/api/logout" -Method Post -WebSession $roleWeb -Headers $headers
    Write-Output "OK $demoUser : sesión, seis consultas, alcance de datos y cierre de sesión."
}
Write-Output 'Los cuatro usuarios demo funcionan; no se modificaron datos del negocio.'
