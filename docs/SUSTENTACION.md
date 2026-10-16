# Guion de sustentación PC2 — 15 minutos

## Preparación

1. Arrancar Docker y ejecutar el proyecto con perfil `demo` antes de la exposición.
2. Entrar como supervisor. Si cambió el día desde la carga inicial, pulsar Asignar cartera.
3. Tener abiertas la aplicación, el IDE, `docs/PATRONES.md` y los resultados de `mvnw test`.
4. Elegir una deuda con saldo y teléfono activo. Anotar su cliente; servirá para encontrar la promesa.
5. Ensayar sin depender de Internet: una vez descargadas las dependencias y la imagen, la demo trabaja localmente.

## Diapositivas y tiempos

| Lámina | Tiempo | Contenido / acción |
|---|---|---|
| 1 | 0:00–0:45 | COBRA360, integrantes y URL real del repositorio. |
| 2 | 0:45–2:00 | Problema, alcance y stack. Explicar las tres pantallas de PC1 ahora funcionales. |
| 3 | 2:00–4:00 | UML de cinco creacionales y clases reales. Distinguir Factory Method de Abstract Factory. |
| 4 | 4:00–6:00 | UML de cuatro estructurales y sus operaciones de negocio. |
| 5 | 6:00–10:30 | Demo de gestión, copia de guion, promesa y pago parcial/completo. |
| 6 | 10:30–13:30 | Reportes, core simulado, centro técnico, auditoría y Flyway. |
| 7 | 13:30–15:00 | Lecciones, decisiones, pruebas, alcance pendiente y cierre. |

## Recorrido de la demo

1. Mostrar Resumen y explicar que los indicadores proceden de MySQL, no de números escritos en la pantalla.
2. Mostrar la cartera del supervisor y su asignación por perfil; el botón no duplica deudas ya asignadas hoy.
3. Abrir una deuda con saldo. Revisar datos, teléfono e historial.
4. Pulsar Usar copia de guion base. Explicar **Prototype** y editar el texto para describir un contacto concreto.
5. Seleccionar “Acepta pagar”, registrar una promesa de S/ 200 con fecha de hoy o mañana y guardar.
6. Mostrar en código el creador de la acción (**Factory Method**), la familia por tramo (**Abstract Factory**) y el límite único (**Singleton**).
7. Buscar al cliente en Promesas. Registrar S/ 80; comprobar `PARCIAL`. Registrar los S/ 120 restantes; comprobar `CUMPLIDA` y el saldo actualizado.
8. Mostrar Auditoría: **Facade** confirma la operación y **Decorator** añade pie/auditoría a la notificación interna.
9. Abrir Reportes: inspeccionar construcción con **Builder** y suma recursiva con **Composite**.
10. Si queda tiempo, registrar un abono sin promesa con “core simulado”: explicar conversión de centavos, referencia e idempotencia de **Adapter**.
11. Abrir Flyway en el centro técnico y mostrar V1–V3 aplicadas. Explicar que un reinicio valida y no recrea todo.
12. Cerrar sesión y entrar como `mirko`: comprobar que ve su propia cartera y que no tiene acceso al centro técnico.

## Preguntas técnicas que deben poder responder

**¿Qué parte viene de la PC1?**

El dominio, los actores, las diez entidades, la arquitectura de capas y los flujos de cartera, gestión y promesas. La PC2 convierte ese diseño en código funcional con nueve patrones, seguridad, persistencia y pruebas.

**¿Por qué MySQL y Spring Boot?**

MySQL 8.0 ya fue elegido en la PC1. Spring Boot permite organizar los servicios Java, inyectar dependencias y configurar transacciones, seguridad y persistencia en un único proyecto.

**¿Por qué Flyway y no `ddl-auto=update`?**

Porque el esquema debe tener versiones reproducibles y auditables. Flyway aplica scripts pendientes y verifica checksums. Hibernate solo valida que sus entidades correspondan al esquema.

**¿Qué ocurre si editan una migración ya aplicada?**

La validación puede fallar por cambio de checksum. El cambio normal se agrega como V4, V5, etc. No se deben borrar historiales ni usar `repair` para ocultar diferencias sin entenderlas.

**¿Singleton es lo mismo que un servicio de Spring?**

No exactamente. Spring suele mantener una instancia por contenedor. Aquí se muestra explícitamente el patrón con un enum de un miembro para configuración. No se utiliza un singleton manual para conexiones: hay un pool.

**¿Prototype duplica registros del cliente?**

No. Copia una plantilla de guion. Su lista es independiente; no clona IDs, deudas ni compromisos existentes.

**¿Qué diferencia Factory Method de Abstract Factory en su código?**

`CreadorAccion` tiene un método fábrica sobrescrito que produce una acción. `FamiliaCobranza` crea un conjunto de dos productos relacionados: mensaje y política del tramo.

**¿Las tres políticas tienen descuentos distintos?**

No. El prototipo usa los mismos límites base de compromiso y mensajes diferentes. No implementa políticas reales del banco ni inventa autorizaciones de descuento.

**¿Qué prueba que Decorator no sea solo herencia?**

El decorador contiene otro `Notificador` y delega la operación. Los envoltorios se componen en tiempo de ejecución; todos conservan el mismo contrato.

**¿Qué hace Facade si algo falla?**

La transacción revierte los cambios de la operación. La fachada permite al controlador solicitar un solo caso de uso en vez de coordinar sus efectos por separado.

**¿Qué pasa si dos asesores intentan pagar la misma deuda al mismo tiempo?**

Se bloquea la fila de la deuda. El segundo pago valida contra el saldo actualizado después de que termine el primero. La prueba de concurrencia comprueba que no se produce saldo negativo.

**¿Cómo evitan pagos duplicados del core?**

Mediante una referencia de operación y una restricción única. La referencia se revisa bajo el bloqueo de la deuda y la base también impide duplicados.

**¿Hay conexión con BBVA o envío de correos?**

No. El caso de estudio es académico. El core es simulado y las notificaciones se registran internamente. Debe indicarse expresamente durante la exposición.

**¿Qué falta para el sistema final?**

Integraciones reales autorizadas, mantenimiento completo de datos/usuarios y pruebas con usuarios para validar las metas del negocio, además del despliegue y las necesidades que el grupo acuerde para la entrega final.

## Reparto sugerido entre dos integrantes

Una persona explica negocio, arquitectura y creacionales; la otra estructurales y demostración. Ambas deben poder explicar promesas, pagos, Flyway, autorización y las decisiones del código. No basta con memorizar las diapositivas.
