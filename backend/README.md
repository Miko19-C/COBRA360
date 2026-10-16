# Backend COBRA360

Java 17, Spring Boot 3.5.13, MySQL 8.0.44, Flyway, Spring Data JPA, JDBC, Spring Security y Bean Validation. Compilación gestionada por Maven desde el proyecto padre.

## Capas

| Carpeta Java | Responsabilidad |
|---|---|
| `web/` | Siete controladores por funcionalidad y traducción de errores HTTP. |
| `application/` | Casos de uso, transacciones y autorización de negocio. |
| `domain/model/` | Diez entidades del modelo aprobado en PC1. |
| `domain/patterns/` | Creacionales y árbol Composite. |
| `domain/ports/` | Interfaces que consumen los casos de uso. |
| `infrastructure/` | Implementaciones de puertos y configuración técnica. |

## Módulos de aplicación

| Módulo | Servicios |
|---|---|
| `cartera` | Consulta autorizada y asignación de deudas por perfil. |
| `gestion` | Fachada de registro, creación de promesas y copia de guion. |
| `pagos` | Registro atómico de abonos y conciliación del core simulado. |
| `promesas` | Actualización de compromisos vencidos. |
| `reportes` | Indicadores, Builder del reporte, Composite y CSV. |
| `tecnico` | Evidencias de patrones, migraciones y auditoría. |
| `shared/security` | Validación de usuario y propiedad de cartera mediante un puerto de identidad. |
| `shared/audit` | Registro de acciones mediante un puerto de auditoría. |
| `command` | Comandos de entrada con validaciones. |

Los servicios no importan clases de `web`, infraestructura, JDBC ni Spring Data. La configuración concreta de los decoradores está en `infrastructure/notifications/NotificacionConfig`; `RegistroGestionService` recibe el contrato `Notificador`.

## Infraestructura

- `repository/`: adaptadores de escritura Spring Data JPA.
- `query/`: consultas SQL parametrizadas y agregaciones de lectura.
- `security/`: sesión, BCrypt, CSRF e implementación de identidad actual.
- `audit/`: almacenamiento de eventos.
- `migrations/`: consulta del historial real de Flyway.
- `notifications/`: componente y decoradores de la bandeja interna.
- `core/`: Adapter del contrato legado simulado.
- `scheduling/`: planificación del proceso de promesas vencidas.
- `demo/`: datos académicos exclusivamente con perfil `demo`.

## Reglas de negocio

- El asesor solo puede gestionar sus asignaciones del día. Para registrar pagos debe tener la deuda asignada hoy.
- Una gestión requiere deuda con saldo y un teléfono del cliente si se proporciona uno.
- Los teléfonos errados/fuera de servicio no se reutilizan en el flujo de contacto.
- Una promesa debe ser positiva, no superar el saldo y tener fecha entre hoy y 90 días posteriores. Los 90 días son una decisión del prototipo.
- Un pago no puede ser futuro, anterior al vencimiento ni mayor que el saldo.
- Si se vincula a una promesa, el pago debe corresponder a esa deuda, no anteceder a su gestión y no superar el compromiso pendiente.
- Los pagos con referencia externa repetida se rechazan.
- Se utiliza `BigDecimal`, `DECIMAL(12,2)` y bloqueo pesimista de la deuda para serializar pagos concurrentes.
- El proceso de contacto y sus efectos se confirma en una transacción desde la fachada.
- La zona del negocio es Lima; Java usa un `Clock` y SQL usa `-05:00`.

## Migraciones

Los scripts permanecen en `src/main/resources/db/migration`. No se modificaron los checksums al reorganizar el proyecto:

1. V1: entidades PC1, restricciones, índices y vista.
2. V2: catálogos, usuarios y auditoría.
3. V3: referencia idempotente del abono externo.

Flyway es el único creador del esquema; Hibernate lo valida. Los fixtures dinámicos del perfil demo se generan en Java para calcular fechas relativas y BCrypt.

## Ejecutar desde la raíz del repositorio

```powershell
docker compose up -d --wait
.\mvnw.cmd -pl backend spring-boot:run "-Dspring-boot.run.profiles=demo"
```

Maven copia el frontend a los recursos del ejecutable, manteniendo `frontend/` como fuente única. Para trabajar la interfaz sin recompilar, usa su servidor de desarrollo en 5173.

## Pruebas

`CobranzaIntegrationTest` utiliza MySQL real temporal con Testcontainers; `PatronesTest` verifica colaboraciones de los patrones; `ArquitecturaTest` protege las dependencias entre capas y módulos. Ejecuta `../mvnw.cmd verify` desde esta carpeta o `./mvnw.cmd verify` desde la raíz.

Se conserva JPA en las entidades del dominio como decisión pragmática del proyecto académico. La inversión de dependencias se exige en puertos y casos de uso; el modelo de persistencia no se presenta como totalmente independiente del ORM.
