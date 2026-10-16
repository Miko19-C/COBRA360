-- Referencia única del core: evita aplicar el mismo abono externo dos veces.
ALTER TABLE pago ADD COLUMN referencia_externa VARCHAR(100) NULL;
ALTER TABLE pago ADD CONSTRAINT uq_pago_referencia UNIQUE(referencia_externa);
