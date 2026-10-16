# Despliegue temporal en AWS

La demo utiliza una instancia EC2 Ubuntu 24.04 t3.small en us-east-1, disco gp3 cifrado de 20 GB y CPU en modo standard. MySQL 8.0.44 y Spring Boot comparten el servidor. Caddy publica HTTPS y redirige HTTP.

## Preparación

1. Compilar desde la raíz con `./mvnw.cmd verify`.
2. Copiar el JAR generado como `cobra360.jar` junto a los archivos de esta carpeta en el servidor.
3. Crear `.env` exclusivamente en el servidor con `APP_DOMAIN`, `DB_PASSWORD`, `MYSQL_ROOT_PASSWORD` y `DEMO_PASSWORD` aleatorios. No versionar ese archivo.
4. El dominio debe resolver a la IP del servidor. Permitir 80 y 443 públicos, y SSH 22 solo desde la IP del operador. No publicar MySQL ni 8080.
5. Ejecutar `sudo docker compose up -d --build`.
6. Comprobar `/actuator/health`, ingreso y alcance de datos de cada rol.

`DEMO_PASSWORD` cambia la contraseña de las cuentas al inicializar una base vacía. No cambia usuarios existentes. La configuración local conserva su valor predeterminado. La base cloud usa datos ficticios nuevos; la base local no se copia ni modifica.

## Duración y costos

Se programó apagado tras 24 horas en cloud-init. El apagado detiene la instancia pero no elimina el disco EBS. Al terminar la demo, terminar la instancia y comprobar que se eliminó su disco raíz; retirar la llave y el grupo de seguridad de la demo si ya no se necesitan. No hay RDS, balanceador, NAT Gateway ni Elastic IP reservada.

La dirección temporal basada en sslip.io depende del DNS de un tercero y de la IP pública asignada. Si se detiene y vuelve a iniciar la instancia, verificar la IP y actualizar APP_DOMAIN. Para uso permanente utilizar un dominio controlado y diseñar copias de seguridad.
