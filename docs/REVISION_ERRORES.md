# Revisión del backend y frontend

Fecha: 16 de octubre de 2026. Revisión del código, pruebas con MySQL real y comprobación de la interfaz local.

## Errores corregidos

| Prioridad | Problema encontrado | Corrección y comprobación |
|---|---|---|
| Alta | La consulta de promesas concatenaba `?group by` al filtrar por asesor. MySQL devolvía un error e impedía cargar también el resumen y los reportes de ese rol. | Separación correcta de las cláusulas. Pruebas de `/api/promesas`, `/api/resumen` y `/api/reportes` con asesor y supervisor. |
| Alta | Al cerrar sesión o cambiar de usuario podían conservarse datos, filtros o una respuesta pendiente del usuario anterior; un detalle pendiente podía volver a abrirse. | Limpieza del estado y diálogos al cambiar de identidad, versiones de inicialización y comprobación de sesión antes de aplicar respuestas o renderizar. Pruebas con respuestas demoradas. |
| Media | Una promesa pendiente aparecía como «Completa» si su deuda se había cancelado mediante pagos sin vínculo con ella. | Solo `CUMPLIDA` muestra «Completa»; las restantes muestran «Deuda sin saldo» cuando corresponde, conservando su estado y trazabilidad. |
| Media | El asesor veía botones para cobrar promesas históricas sin tener hoy la deuda asignada; el servidor rechazaba la operación. | La API informa `puede_registrar_pago` según rol y asignación actual. La interfaz muestra «Sin asignación vigente». Se mantiene la validación del servidor. |
| Media | En pagos, la búsqueda ofrecía buscar por documento pero el listado no lo devolvía. | La consulta devuelve `nro_documento`, la tabla lo muestra y la búsqueda puede encontrarlo. |
| Media | El formulario exigía teléfono aunque el backend admitía contacto por otro canal; con todos los teléfonos inhabilitados no se podía continuar. | Opción explícita «Sin teléfono / contacto por otro canal»; los teléfonos errados o fuera de servicio siguen deshabilitados y se rechazan en el servidor. |
| Baja | La interfaz admitía fechas de promesa superiores al límite de 90 días y solo informaba el error al guardar. | El detalle publica la fecha máxima calculada por el backend; el formulario la utiliza como `max`, además del mínimo. El servidor conserva la validación autoritativa. |
| Media | Identificadores inválidos, métodos incorrectos, tipos de contenido no admitidos y rutas inexistentes terminaban en la captura genérica de error 500. | Respuestas 400, 405 con cabecera Allow, 415 y 404 respectivamente. Prueba de regresión de los cuatro casos. |

## Verificación reproducible

```powershell
.\mvnw.cmd -q verify
npm --prefix frontend run check
npm --prefix frontend test
.\scripts\smoke-test.ps1
.\scripts\smoke-test.ps1 -BaseUrl http://127.0.0.1:5173
.\scripts\verificar-roles.ps1
```

Las pruebas de MySQL usan una base temporal aislada con Testcontainers. No se borraron los datos de la demostración local ni se editaron las migraciones aplicadas.

## Alcance y límites

Se revisaron acceso y cambio de usuario, consultas por rol, cartera, promesas, pagos, validaciones HTTP, reportes, arquitectura y migraciones. Los resultados están detallados en `VERIFICACION.md`; las pruebas no garantizan ausencia absoluta de errores ni una nota académica. La publicación del repositorio, el informe formal, las diapositivas y la sustentación siguen siendo entregables separados. Las integraciones externas continúan simuladas.
