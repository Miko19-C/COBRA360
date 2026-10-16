-- Modelo PC1: diez entidades de negocio; Flyway no elimina bases existentes.
CREATE TABLE perfil_riesgo (
 id_perfil BIGINT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(30) NOT NULL UNIQUE,
 tramo_habilitado VARCHAR(20) NOT NULL,
 CONSTRAINT ck_perfil_tramo CHECK (tramo_habilitado IN ('TEMPRANA','TARDIA','CASTIGO','TODOS'))
) ENGINE=InnoDB;
CREATE TABLE asesor (
 id_asesor BIGINT AUTO_INCREMENT PRIMARY KEY, id_perfil BIGINT NOT NULL,
 nombre VARCHAR(100) NOT NULL, usuario VARCHAR(30) NOT NULL UNIQUE, estado CHAR(1) NOT NULL DEFAULT 'A',
 CONSTRAINT fk_asesor_perfil FOREIGN KEY (id_perfil) REFERENCES perfil_riesgo(id_perfil),
 CONSTRAINT ck_asesor_estado CHECK (estado IN ('A','I'))
) ENGINE=InnoDB;
CREATE TABLE cliente (
 id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY, tipo_documento VARCHAR(10) NOT NULL,
 nro_documento VARCHAR(20) NOT NULL, nombre VARCHAR(100) NOT NULL, correo VARCHAR(150),
 CONSTRAINT uq_documento UNIQUE(tipo_documento,nro_documento),
 CONSTRAINT ck_documento CHECK (tipo_documento IN ('DNI','CE','RUC'))
) ENGINE=InnoDB;
CREATE TABLE telefono (
 id_telefono BIGINT AUTO_INCREMENT PRIMARY KEY, id_cliente BIGINT NOT NULL, numero VARCHAR(20) NOT NULL,
 estado VARCHAR(25) NOT NULL DEFAULT 'ACTIVO', es_principal BOOLEAN NOT NULL DEFAULT FALSE,
 CONSTRAINT fk_telefono_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE,
 CONSTRAINT uq_telefono UNIQUE(id_cliente,numero),
 CONSTRAINT ck_telefono_estado CHECK (estado IN ('ACTIVO','ERRADO','NO_CONTESTA','FUERA_SERVICIO'))
) ENGINE=InnoDB;
CREATE TABLE deuda (
 id_deuda BIGINT AUTO_INCREMENT PRIMARY KEY, id_cliente BIGINT NOT NULL, producto VARCHAR(80) NOT NULL,
 monto_total DECIMAL(12,2) NOT NULL, saldo_actual DECIMAL(12,2) NOT NULL,
 fecha_vencimiento DATE NOT NULL, tramo_mora VARCHAR(20) NOT NULL,
 CONSTRAINT fk_deuda_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
 CONSTRAINT ck_deuda_montos CHECK (monto_total > 0 AND saldo_actual >= 0 AND saldo_actual <= monto_total),
 CONSTRAINT ck_deuda_tramo CHECK (tramo_mora IN ('TEMPRANA','TARDIA','CASTIGO'))
) ENGINE=InnoDB;
CREATE TABLE asignacion (
 id_asignacion BIGINT AUTO_INCREMENT PRIMARY KEY, id_deuda BIGINT NOT NULL, id_asesor BIGINT NOT NULL,
 fecha_asignacion DATE NOT NULL, estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
 CONSTRAINT fk_asignacion_deuda FOREIGN KEY(id_deuda) REFERENCES deuda(id_deuda),
 CONSTRAINT fk_asignacion_asesor FOREIGN KEY(id_asesor) REFERENCES asesor(id_asesor),
 CONSTRAINT uq_asignacion UNIQUE(id_deuda,fecha_asignacion),
 CONSTRAINT ck_asignacion_estado CHECK (estado IN ('PENDIENTE','GESTIONADA'))
) ENGINE=InnoDB;
CREATE TABLE tipo_resultado (
 id_tipo_resultado BIGINT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(100) NOT NULL UNIQUE,
 es_contacto_efectivo BOOLEAN NOT NULL, genera_promesa BOOLEAN NOT NULL
) ENGINE=InnoDB;
CREATE TABLE gestion (
 id_gestion BIGINT AUTO_INCREMENT PRIMARY KEY, id_asignacion BIGINT NOT NULL,
 id_tipo_resultado BIGINT NOT NULL, id_telefono BIGINT,
 fecha_contacto DATETIME(6) NOT NULL, observacion VARCHAR(1000) NOT NULL,
 CONSTRAINT fk_gestion_asignacion FOREIGN KEY(id_asignacion) REFERENCES asignacion(id_asignacion),
 CONSTRAINT fk_gestion_resultado FOREIGN KEY(id_tipo_resultado) REFERENCES tipo_resultado(id_tipo_resultado),
 CONSTRAINT fk_gestion_telefono FOREIGN KEY(id_telefono) REFERENCES telefono(id_telefono) ON DELETE SET NULL
) ENGINE=InnoDB;
CREATE TABLE promesa_pago (
 id_promesa BIGINT AUTO_INCREMENT PRIMARY KEY, id_gestion BIGINT NOT NULL UNIQUE,
 monto_comprometido DECIMAL(12,2) NOT NULL, fecha_compromiso DATE NOT NULL, estado VARCHAR(20) NOT NULL,
 CONSTRAINT fk_promesa_gestion FOREIGN KEY(id_gestion) REFERENCES gestion(id_gestion),
 CONSTRAINT ck_promesa_monto CHECK(monto_comprometido > 0),
 CONSTRAINT ck_promesa_estado CHECK(estado IN ('PENDIENTE','PARCIAL','CUMPLIDA','INCUMPLIDA'))
) ENGINE=InnoDB;
CREATE TABLE pago (
 id_pago BIGINT AUTO_INCREMENT PRIMARY KEY, id_deuda BIGINT NOT NULL, id_promesa BIGINT,
 monto DECIMAL(12,2) NOT NULL, fecha_pago DATE NOT NULL,
 CONSTRAINT fk_pago_deuda FOREIGN KEY(id_deuda) REFERENCES deuda(id_deuda),
 CONSTRAINT fk_pago_promesa FOREIGN KEY(id_promesa) REFERENCES promesa_pago(id_promesa),
 CONSTRAINT ck_pago_monto CHECK(monto > 0)
) ENGINE=InnoDB;
CREATE INDEX ix_asignacion_fecha_asesor ON asignacion(fecha_asignacion,id_asesor);
CREATE INDEX ix_gestion_asignacion_fecha ON gestion(id_asignacion,fecha_contacto);
CREATE INDEX ix_promesa_fecha_estado ON promesa_pago(fecha_compromiso,estado);
CREATE INDEX ix_pago_promesa ON pago(id_promesa);
CREATE VIEW v_cartera_dia AS
 SELECT a.id_asignacion,a.fecha_asignacion,a.id_asesor,c.nombre AS cliente,c.nro_documento,
 d.id_deuda,d.producto,d.saldo_actual,d.tramo_mora,
 GREATEST(0,DATEDIFF(CURRENT_DATE,d.fecha_vencimiento)) AS dias_atraso,a.estado
 FROM asignacion a JOIN deuda d ON d.id_deuda=a.id_deuda JOIN cliente c ON c.id_cliente=d.id_cliente;
