-- V9__create_inventory_ledger.sql
-- Kardex / auditoría de inventario por producto y bodega

-- NOTA: Asume que ya existen las tablas:
--   - products(id)
--   - warehouses(id)
-- y la función set_updated_at() creada en una migración previa (no se usa aquí).

CREATE TABLE IF NOT EXISTS inventory_ledger (
  id              BIGSERIAL PRIMARY KEY,

  product_id      BIGINT NOT NULL,
  warehouse_id    BIGINT NOT NULL,

  -- Tipo de movimiento (usa CHECK en vez de enum para simpleza en migraciones)
  movement_type   VARCHAR(32) NOT NULL,
  -- Cantidad que se suma o resta al stock (positiva o negativa, no cero)
  qty_delta       INTEGER NOT NULL,
  -- Saldo resultante en la bodega después del movimiento (no negativo)
  balance_after   INTEGER NOT NULL,

  -- Enlaces de referencia al origen del movimiento (orden, ajuste, transferencia, reserva, etc.)
  reference_type  VARCHAR(64),      -- p.ej.: 'INBOUND_ORDER', 'OUTBOUND_ORDER', 'ADJUSTMENT', 'TRANSFER', 'RESERVATION'
  reference_id    VARCHAR(64),      -- UUID o ID externo en texto (flexible)

  note            TEXT,
  created_by      VARCHAR(64),      -- usuario/servicio que generó el movimiento (opcional)

  created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

  CONSTRAINT fk_ledger_product   FOREIGN KEY (product_id)   REFERENCES products(id),
  CONSTRAINT fk_ledger_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),

  CONSTRAINT ck_ledger_movement_type CHECK (
    movement_type IN (
      'INBOUND',       -- ingreso (recepción)
      'OUTBOUND',      -- salida (despacho)
      'ADJUSTMENT',    -- ajuste manual (+/-)
      'TRANSFER_OUT',  -- transferencia: sale de bodega origen
      'TRANSFER_IN',   -- transferencia: entra a bodega destino
      'RESERVE',       -- reserva (afecta disponible, si lo implementas)
      'RELEASE'        -- liberación de reserva
    )
  ),
  CONSTRAINT ck_ledger_qty_nonzero   CHECK (qty_delta <> 0),
  CONSTRAINT ck_ledger_balance_nonneg CHECK (balance_after >= 0)
);

-- Índices para consultas típicas (kardex, auditoría, referencias)
CREATE INDEX IF NOT EXISTS idx_ledger_prod_wh_time
  ON inventory_ledger (product_id, warehouse_id, created_at);

CREATE INDEX IF NOT EXISTS idx_ledger_prod_time
  ON inventory_ledger (product_id, created_at);

CREATE INDEX IF NOT EXISTS idx_ledger_ref
  ON inventory_ledger (reference_type, reference_id);

-- Sugerencia de vista (opcional, descomenta si la quieres)
-- CREATE VIEW v_inventory_ledger_recent AS
-- SELECT *
-- FROM inventory_ledger
-- WHERE created_at > NOW() - INTERVAL '90 days';
