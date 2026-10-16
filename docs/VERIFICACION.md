# Verificación de la implementación

Fecha: 16 de octubre de 2026. Entorno: Windows, Java 17, Docker Desktop y MySQL 8.0.44.

## Compilación y pruebas

Comando final de la revisión: `./mvnw.cmd -q verify` (con limpieza de compilación realizada previamente).

Resultado: **40 pruebas, 0 fallos, 0 errores, 0 omitidas**.

| Conjunto | Pruebas | Qué verifica |
|---|---:|---|
| `CobranzaIntegrationTest` | 26 | MySQL real, Flyway, seguridad, reglas del dominio, persistencia, atomicidad, concurrencia y módulos web públicos. |
| `PatronesTest` | 7 | Copia profunda, productos de fábrica, familias, inmutabilidad del builder, adapter, decorator y árbol recursivo. |
| `ArquitecturaTest` | 7 | Dependencias entre capas, puertos independientes, servicios y repositorios en su capa, módulos sin ciclos. |

Las pruebas de integración crean su propio MySQL temporal mediante Testcontainers. Se comprobaron especialmente:

- Segundo arranque sin reaplicar las tres migraciones.
- Sesión real con BCrypt; acceso anónimo bloqueado; validación de CSRF.
- Asesor sin acceso a detalle/pagos de cartera ajena ni funciones del supervisor.
- Promesas inválidas sin registros de negocio nuevos.
- Teléfono de otro cliente rechazado y teléfono errado inutilizable en gestiones posteriores.
- Pago parcial y completo con actualización de promesa/saldo.
- Rechazo de pagos futuros, excesivos o vinculados a una promesa de otra deuda.
- Referencia externa repetida sin doble abono.
- Dos pagos concurrentes de S/ 80 sobre saldo S/ 100: solo uno se confirma y el saldo termina en S/ 20.
- Falla inducida en la auditoría final: gestión, promesa, cambio de asignación y eventos previos se revierten juntos.
- Promesa vencida calculada correctamente aunque no haya corrido el proceso nocturno.
- Asignación diaria por perfil, sin duplicados.
- Reporte, exportación y nueve evidencias de patrones.

Los resultados reproducibles están en `backend/target/surefire-reports` después de ejecutar las pruebas. Esa carpeta no se versiona.

La revisión y las correcciones posteriores se detallan en [REVISION_ERRORES.md](REVISION_ERRORES.md). `npm --prefix frontend test` verifica nueve casos de regresión del frontend, incluidos el cambio de sesión con respuestas demoradas y las acciones de promesas.

El comando `npm --prefix frontend run check` verificó sintaxis, importaciones, exportaciones y ausencia de ciclos en 20 módulos JavaScript, incluidos sus scripts de desarrollo.

## Ejecutable

Se inició `backend/target/cobra360-1.0.0.jar` con perfil `demo`. La verificación HTTP `scripts/smoke-test.ps1` autenticó al supervisor y consultó correctamente cartera, promesas, pagos, resumen, reportes, catálogos, patrones, migraciones, auditoría y copia de plantilla, sin modificar datos del negocio.

La misma verificación se repitió sobre `http://127.0.0.1:5173`, con el frontend en un proceso independiente y la API en 8080. Las diez rutas y la sesión/CSRF funcionaron también a través del proxy local. Se comprobó en el navegador el ingreso, el panel y los reportes tras la modularización, y la carga del panel desde 5173.

## Interfaz

Se revisaron el acceso, el panel en tamaño móvil y escritorio, la cartera y el formulario de contacto. Se creó desde el navegador una gestión de prueba con promesa de S/ 250 para el cliente ficticio Mateo Cárdenas; la cartera cambió a Gestionada y el compromiso apareció en Seguimiento. La prueba permanece en la base local de demostración, pero no forma parte de los fixtures de una instalación nueva.

Se registraron desde la interfaz dos abonos sobre esa promesa: S/ 100 locales y S/ 150 a través del Adapter simulado. Se verificaron los estados Parcial y Cumplida, el importe acumulado de S/ 250 y la reducción del saldo de S/ 10 680 a S/ 10 430. La búsqueda por nombre también se comprobó en la versión final.

La verificación funcional no constituye una medición de las metas de negocio ni una auditoría de producción. No se ejecutaron integraciones bancarias o de mensajería reales.

## Capturas guardadas

- [Panel y recuperación](capturas/dashboard.jpg).
- [Promesas cumplidas](capturas/promesa-cumplida.jpg).
- [Nueve patrones](capturas/patrones.jpg).
- [Migraciones de Flyway](capturas/flyway.jpg).
- [Frontend modular ejecutado por separado](capturas/frontend-modular.jpg).

## Revisión posterior de errores

Resultado final: 40 pruebas del backend (26 de integración, 7 de patrones y 7 de arquitectura) y 9 pruebas del frontend, todas sin fallos ni errores. Se verificaron sintaxis y dependencias de los 20 módulos JavaScript.

Se ejecutaron `scripts/verificar-roles.ps1` y `scripts/smoke-test.ps1` sobre 8080 y 5173: supervisor, Mirko, Lucía y Diego ingresaron, consultaron sus datos y cerraron sesión. Para cada asesor se comprobó el alcance de cartera/promesas y la respuesta 403 de auditoría.

En el navegador se comprobó el panel de Mirko con ocho deudas, el formulario con contacto sin teléfono y fecha máxima 2027-01-14 (90 días desde el día de revisión), la búsqueda de un pago por DNI 90000010, el cierre de sesión y el ingreso como supervisor con 24 deudas. Se utilizó el menú en la pantalla estrecha. No se guardaron gestiones ni pagos nuevos en esta revisión visual. La consola no mostró errores en la comprobación final.

Evidencia: [versión corregida](capturas/revision-corregida.jpg). Catálogo de hallazgos y soluciones: [revisión de errores](REVISION_ERRORES.md).

## Menú móvil

Se añadió un fondo que cierra el menú al tocar fuera e intercepta el toque para no activar el contenido posterior. Verificado en navegador: abrir/cerrar fuera, Escape y navegación a Reportes; `aria-expanded` termina en `false` y el fondo desaparece. El estado también se reinicia al cambiar de usuario. Sintaxis y nueve regresiones del frontend aprobadas. Ejecutable actualizado con `mvnw.cmd -q -DskipTests package`, pues no cambió código Java. Evidencia: [menú cerrado después de navegar](capturas/menu-movil-cerrado.jpg).
