-- V12__inbound_orders_add_warehouse_and_receive_fields.sql
-- Objetivo:
-- 1) Añadir columna warehouse_id a inbound_orders y poblarla con MAIN.
-- 2) Añadir columnas de auditoría de recepción.
-- 3) Añadir FK, NOT NULL e índices.

-- 1) Columnas nuevas (permitir NULL temporalmente para backfill)
ALTER TABLE inbound_orders
  ADD COLUMN warehouse_id BIGINT,
  ADD COLUMN received_at TIMESTAMPTZ,
  ADD COLUMN received_by VARCHAR(100);

-- 2) Backfill: asignar MAIN a órdenes existentes
--    Asume que ya existe warehouses(code UNIQUE) y un registro con code='MAIN'
UPDATE inbound_orders io
SET warehouse_id = w.id
FROM warehouses w
WHERE w.code = 'MAIN'
  AND io.warehouse_id IS NULL;

-- 3) Asegurar que todas las filas tienen warehouse_id (en caso de que no exista MAIN, fallará)
--    Si faltara MAIN, crea una migración previa para insertarlo, pero tú ya lo tienes.
--    Ahora forzamos NOT NULL y la FK.
ALTER TABLE inbound_orders
  ALTER COLUMN warehouse_id SET NOT NULL;

-- 4) Clave foránea hacia warehouses
ALTER TABLE inbound_orders
  ADD CONSTRAINT fk_inbound_orders_warehouse
  FOREIGN KEY (warehouse_id) REFERENCES warehouses (id);

-- 5) Índices útiles para consultas
CREATE INDEX IF NOT EXISTS idx_inbound_warehouse ON inbound_orders (warehouse_id);
CREATE INDEX IF NOT EXISTS idx_inbound_status    ON inbound_orders (status);

-- (Opcional) Si no existiera el índice por order_number (para búsquedas), lo creamos:
CREATE INDEX IF NOT EXISTS idx_inbound_order_number ON inbound_orders (order_number);
