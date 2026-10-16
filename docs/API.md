# API HTTP de COBRA360

Base local: `http://127.0.0.1:8080/api`. JSON UTF-8. Sesiones mediante cookie HTTP; no se almacenan JWT en localStorage.

## Sesión y CSRF

1. `GET /session` devuelve `authenticated`, `csrf` y `csrfHeader`; si hay sesión, incluye `user`.
2. `POST /login` recibe formulario `username` / `password`, cookie de la solicitud anterior y el header CSRF.
3. Después del login vuelve a consultar `/session`: Spring rota el token de seguridad.
4. Envía el token y cookie en cada POST. `POST /logout` cierra la sesión.

Ejemplo PowerShell (solo datos demo locales):

```powershell
$session = Invoke-RestMethod http://127.0.0.1:8080/api/session -SessionVariable web
$headers = @{}
$headers[$session.csrfHeader] = $session.csrf
Invoke-RestMethod http://127.0.0.1:8080/api/login -Method Post -WebSession $web -Headers $headers -Body @{username='supervisor';password='Cobra360!2026'}
$session = Invoke-RestMethod http://127.0.0.1:8080/api/session -WebSession $web
$headers[$session.csrfHeader] = $session.csrf
Invoke-RestMethod http://127.0.0.1:8080/api/cartera -WebSession $web
```

## Rutas

| Método | Ruta | Permiso / comportamiento |
|---|---|---|
| GET | `/session` | Pública. Token y estado de sesión. |
| POST | `/login` | Pública con CSRF. Autenticación. |
| POST | `/logout` | Cierra sesión, requiere CSRF. |
| GET | `/catalogos` | Resultados, asesores habilitados para supervisor, fecha de Lima. |
| GET | `/resumen` | Indicadores y recuperación de la última semana. |
| GET | `/cartera` | Asignaciones del día, limitadas por usuario. |
| GET | `/cartera/{idAsignacion}` | Datos, teléfonos e historial; valida propiedad. |
| POST | `/gestiones` | Registra caso de uso mediante Facade. |
| GET | `/promesas` | Compromisos con pagos acumulados y estado calculado. |
| GET | `/pagos` | Últimos 100 pagos del alcance autorizado. |
| POST | `/pagos` | Pago local validado y transaccional. |
| POST | `/asignaciones/automaticas` | Supervisor. Distribución diaria por perfil. |
| GET | `/reportes` | Reporte Builder y árbol Composite del usuario. |
| GET | `/reportes/cartera.csv` | Exportación CSV UTF-8 con BOM. |
| GET | `/plantillas/gestion` | Copia Prototype de un guion. |
| GET | `/patrones` | Supervisor. Evidencias de nueve patrones. |
| GET | `/migraciones` | Supervisor. Historial real desde Flyway. |
| GET | `/auditoria` | Supervisor. Últimos 100 eventos. |
| POST | `/core/simular` | Supervisor. Abono simulado convertido mediante Adapter. |

## Registrar gestión

Usa IDs que existan en la cartera y catálogos del entorno. Los siguientes son ejemplos sobre los fixtures iniciales; la fecha debe ajustarse al día de prueba.

```json
{
  "asignacionId": 13,
  "tipoResultadoId": 1,
  "telefonoId": 13,
  "observacion": "Titular confirma un compromiso de pago.",
  "montoPromesa": 200.00,
  "fechaCompromiso": "2026-10-17"
}
```

Respuesta: `gestionId`, `promesaId` y `message`. Un resultado que no genera promesa debe enviar `montoPromesa` y `fechaCompromiso` como `null` u omitirlos. El ID de promesa de respuesta es `0` si no se generó una.

## Registrar pago

```json
{
  "deudaId": 13,
  "promesaId": null,
  "monto": 80.00,
  "fechaPago": "2026-10-16"
}
```

El mismo formato sirve para `/core/simular`; en ese caso la referencia de abono procede del adaptador simulado y es determinista para deuda, fecha e importe.

## Errores

- `400`: validación o regla de negocio incumplida.
- `401`: sesión ausente o credenciales incorrectas.
- `403`: rol/propiedad insuficiente o token CSRF inválido.
- `404`: ruta o recurso web inexistente.
- `405`: método HTTP no admitido; incluye `Allow`.
- `409`: conflicto de integridad de la base.
- `415`: tipo de contenido no admitido.
- `500`: error inesperado; el detalle técnico queda en el registro del servidor.

Los errores devuelven `{"message":"..."}`. La API usa el modelo de lectura con nombres de columnas `snake_case` y comandos de escritura en `camelCase`, para separar el contrato de entrada de las consultas SQL.

## Datos de apoyo para formularios

- El detalle `/cartera/{idAsignacion}` incluye `fechaMaximaCompromiso`, calculada en Lima con el límite de 90 días del Singleton. Es un dato de apoyo; el servidor vuelve a validar la fecha al guardar.
- `/promesas` incluye `puede_registrar_pago`: el supervisor puede registrar el abono; el asesor necesita una asignación vigente sobre esa deuda. La interfaz también comprueba saldo y estado; el permiso se vuelve a validar en el comando.
- `/pagos` incluye `nro_documento` para mostrar y buscar el documento del cliente.
- `telefonoId` puede ser `null` para contactos por otro canal. Si se envía, debe pertenecer al cliente y estar habilitado.
