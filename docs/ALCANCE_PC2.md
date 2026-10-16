# Alcance de esta implementación para PC2

La consigna exige un avance funcional del 50 %, pero no define un porcentaje por pantalla o módulo. Este documento describe lo implementado; no asigna arbitrariamente un porcentaje ni garantiza una nota.

## Correspondencia con PC1

| Compromiso original | Implementación disponible | Evidencia |
|---|---|---|
| Cartera morosa del día | Tabla, indicadores, búsqueda, filtros y paginación. | Resumen y Mi cartera. |
| Asignación por tramo/perfil | Distribución a asesores activos del tramo, balanceada por cantidad; evita duplicados diarios. | Supervisor → Asignar cartera. |
| Registro tipificado | Catálogo persistido y formulario de gestión. | Gestionar una deuda. |
| Actualizar teléfono | Errado, fuera de servicio, no contesta o activo según el contacto. | Gestión e historial de teléfonos. |
| Formalizar promesa | Monto y fecha validados, una promesa por gestión. | Resultado “Acepta pagar”. |
| Seguimiento de compromisos | Estados derivados de fecha y pagos; filtro por estado. | Promesas de pago. |
| Vincular pagos | Abonos parciales/completos vinculados o espontáneos sin promesa. | Pagos y seguimiento. |
| BD relacional normalizada | Diez entidades originales, PK/FK/UNIQUE/CHECK, dos tablas técnicas. | Migraciones y DER. |
| Arquitectura y SOLID | Capas, puertos de escritura, responsabilidades separadas. | ARQUITECTURA.md y paquetes Java. |
| Integración con core | Adapter con contrato legado simulado e idempotencia. | Pago → Usar core simulado. |

Las metas de mejora del 15 %, reducción del tiempo del 50 % y registro menor a 30 segundos de la PC1 son objetivos para evaluar con usuarios; no se presentan como resultados obtenidos por el prototipo.

## Correspondencia con la rúbrica PC2

| Criterio | Material preparado | Qué debe hacer el grupo |
|---|---|---|
| Entorno/framework/bibliotecas | Spring Boot, MySQL, JPA, seguridad, validación, logging y Flyway. | Reproducir el arranque y explicar las dependencias. |
| Cinco creacionales | Clases ejecutables, uso en operaciones y UML. | Mostrar código, roles, propósito y resultado. |
| Cuatro estructurales | Adapter, Facade, Decorator y Composite integrados. | Ejecutar gestión, notificación interna, reporte y pago simulado. |
| Calidad/SOLID/repositorio | Organización, README, pruebas y documentación. | Publicar repositorio accesible, revisar autoría y convenciones del curso. |
| Sustentación/demo | Centro técnico, auditoría y guion. | Ensayar y responder individualmente sobre el código. |

## Delimitación para la siguiente entrega

Se mantienen como integraciones simuladas el core bancario y las notificaciones. No están incluidos un proveedor real de correo/SMS, telefonía, lectura de archivos del banco, administración completa de usuarios, recuperación de contraseñas, edición de catálogos, SLA de producción, despliegue público ni analítica histórica avanzada. La gestión de altas/bajas de clientes y deudas se alimenta con fixtures de demo; no hay un CRUD de mantenimiento general.

La asignación usa tramos almacenados en la deuda, como en el modelo de la PC1. Una integración real debe actualizar esos tramos al sincronizar obligaciones. El prototipo no interpreta las segmentaciones académicas como políticas oficiales de BBVA ni como reglas regulatorias.

## Archivos que aún requiere la entrega formal

- Informe Word/PDF con la carátula del grupo, contenido revisado, UML y capturas tomadas del sistema.
- PPT de siete diapositivas siguiendo la consigna.
- URL de GitHub/GitLab/Bitbucket con acceso verificado para el docente.
- Ensayo de 15 minutos y preparación de los 5 minutos de preguntas.

La implementación y los documentos Markdown son base técnica para elaborar esos entregables; no se ha creado un Word o PPT nuevo en esta tarea.
