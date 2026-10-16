# Estructura y evidencia para la rúbrica PC2

## Separación de fuentes

El código Java está en `backend/`; toda la interfaz está en `frontend/`. La documentación y los diagramas viven en `docs/`. El proyecto padre Maven coordina la compilación. Cada área tiene su README y comandos de ejecución.

La aplicación puede usarse en un solo proceso durante la sustentación o con dos procesos durante desarrollo. En ambos casos la interfaz llama a la misma API, con las mismas sesiones, validaciones y autorización.

## Los cinco criterios

| Criterio de 4 puntos | Implementación revisable | Evidencia para presentar |
|---|---|---|
| Entorno, framework y bibliotecas | Spring Boot, MySQL, JPA, JDBC, Security, Validation, logging y Flyway; fuentes separadas y capas con interfaces. | Arranque reproducible, README y estructura de módulos. |
| Cinco creacionales | Singleton, Prototype, Factory Method, Abstract Factory y Builder. | Código, UML en PATRONES.md, guion, gestión y reportes. |
| Cuatro estructurales | Adapter, Facade, Decorator y Composite. | Pago simulado, gestión atómica, notificación auditada y árbol de cartera. |
| Código, SOLID y repositorio | Servicios por caso de uso; SQL y proveedores en adaptadores; puertos de dominio; tests de arquitectura y CI. | `ArquitecturaTest`, pruebas, historial del repositorio y documentación. |
| Sustentación y demo | Flujo funcional con persistencia, roles y centro técnico. | Recorrido de 15 minutos y respuestas sobre el código propio. |

La estructura hace verificables estos criterios. El puntaje también depende de la evaluación del docente, de la aplicación correcta de los patrones y del dominio del código por el grupo.

## Fronteras que se comprueban automáticamente

1. El dominio no depende de las capas externas.
2. Aplicación no importa infraestructura, controladores, JDBC ni Spring Data.
3. Los controladores no acceden a SQL, repositorios concretos o Flyway.
4. Los puertos no dependen de Spring, JPA ni Flyway.
5. Los repositorios Spring Data pertenecen a infraestructura.
6. Las clases de servicio pertenecen a aplicación.
7. Los módulos de aplicación no presentan ciclos de dependencias.

El frontend verifica sintaxis, importaciones, exportaciones y ausencia de ciclos de módulos. Una prueba de integración también comprueba que los módulos públicos se incorporen al ejecutable y puedan cargar antes de iniciar sesión.

## Convención para ampliar el sistema

Para agregar una funcionalidad, incorpora su vista y servicio HTTP en el frontend; crea su controlador, comando/caso de uso y puerto en el backend; implementa el acceso a datos o proveedor en infraestructura. Añade una nueva migración si cambia la base. Mantén la regla de propiedad de cartera y una prueba del comportamiento nuevo.

No edites `backend/target`: es salida generada. Las fuentes originales de la interfaz siempre están en `frontend/`.
