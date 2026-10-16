# Frontend COBRA360

Interfaz de HTML, CSS y JavaScript con módulos ES. Las fuentes están separadas del backend y se comunican exclusivamente por HTTP con `/api`.

## Módulos

| Carpeta | Qué contiene |
|---|---|
| `src/core` | Estado compartido, formatos de moneda/fecha, escape HTML e iconos. |
| `src/services` | Cliente HTTP central con cookies/CSRF y carga de datos. |
| `src/controllers` | Sesión, navegación y registro de eventos. |
| `src/components` | Elementos UI reutilizables, tablas, filtros y diálogos. |
| `src/views` | Pantallas por funcionalidad: resumen, cartera, promesas, pagos, reportes y centro técnico. |
| `src/main.js` | Ensambla controladores e inicia la aplicación. |
| `scripts` | Verificación y servidor local de desarrollo. |

Los módulos de API no importan pantallas. La expiración de sesión se comunica mediante un evento que atiende el punto de entrada. Los controladores reciben navegación/renderizado como dependencias, evitando ciclos entre servicios, sesión y vistas.

## Desarrollo independiente

Requiere Node 20 o superior; no hay dependencias npm que instalar. Con el backend en 8080:

```powershell
npm run dev
```

Abre http://127.0.0.1:5173. Los cambios en fuentes se ven al recargar. El servidor enlaza a loopback y hace proxy de `/api` hacia Spring Boot conservando las cookies y headers de seguridad. Está pensado para desarrollo local.

Puedes ajustar `FRONTEND_PORT` y `BACKEND_URL` mediante variables de entorno del proceso.

## Verificación

```powershell
npm run check
```

Comprueba sintaxis, existencia de imports, nombres exportados y ausencia de dependencias circulares. El CI ejecuta esta comprobación junto con las pruebas del backend.

## Demostración en un ejecutable

El `backend/pom.xml` incorpora `index.html`, CSS, favicon y `src/` al JAR. Por eso la sustentación puede usar solo Spring Boot en 8080. `scripts`, `package.json` y esta documentación no se publican como recursos web. La fuente sigue siendo esta carpeta; no edites copias dentro de `backend/target`.

La autorización efectiva, las validaciones y los saldos se resuelven en el backend. La interfaz presenta los controles correspondientes al rol y muestra los errores del servidor.

## Pruebas de regresión

Ejecutar `npm --prefix frontend test` desde la raíz. Usa `node:test`, sin instalar dependencias, para comprobar estados de promesas, permisos de pago, búsqueda por documento y respuestas pendientes al cambiar de sesión.
