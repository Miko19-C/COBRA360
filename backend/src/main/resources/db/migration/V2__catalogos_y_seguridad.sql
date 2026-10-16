INSERT INTO perfil_riesgo(nombre,tramo_habilitado) VALUES
 ('Junior','TEMPRANA'),('Semi senior','TARDIA'),('Senior','CASTIGO'),('Supervisor','TODOS');
INSERT INTO tipo_resultado(nombre,es_contacto_efectivo,genera_promesa) VALUES
 ('Contacto efectivo - Acepta pagar',TRUE,TRUE),('Contacto efectivo - Niega pago',TRUE,FALSE),
 ('Contacto con tercero',FALSE,FALSE),('No contesta',FALSE,FALSE),('Número errado',FALSE,FALSE),
 ('Teléfono fuera de servicio',FALSE,FALSE);
-- Extensión PC2: acceso y trazabilidad. No altera las diez entidades aprobadas.
CREATE TABLE usuario (
 id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY, usuario VARCHAR(30) NOT NULL UNIQUE,
 password_hash VARCHAR(100) NOT NULL, nombre VARCHAR(100) NOT NULL,
 rol VARCHAR(20) NOT NULL, id_asesor BIGINT,
 CONSTRAINT fk_usuario_asesor FOREIGN KEY(id_asesor) REFERENCES asesor(id_asesor),
 CONSTRAINT ck_usuario_rol CHECK(rol IN ('ASESOR','SUPERVISOR'))
) ENGINE=InnoDB;
CREATE TABLE auditoria (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, usuario VARCHAR(30) NOT NULL,
 accion VARCHAR(100) NOT NULL, detalle VARCHAR(1000) NOT NULL, fecha DATETIME(6) NOT NULL
) ENGINE=InnoDB;
