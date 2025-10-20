-- V14__inbound_order_totals.sql
-- Agrega totalOrdered / totalReceived y totalPending (generada), con backfill inicial

BEGIN;

-- 1) Columnas base
ALTER TABLE inbound_orders
  ADD COLUMN IF NOT EXISTS total_ordered  INTEGER NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS total_received INTEGER NOT NULL DEFAULT 0;

-- 2) Columna generada (PostgreSQL 12+). Si tu versión fuera antigua, avísame y te doy trigger en su lugar.
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_name = 'inbound_orders' AND column_name = 'total_pending'
  ) THEN
    EXECUTE '
      ALTER TABLE inbound_orders
      ADD COLUMN total_pending INTEGER GENERATED ALWAYS AS (GREATEST(total_ordered - total_received, 0)) STORED
    ';
  END IF;
END$$;

-- 3) Backfill de total_ordered desde los items
WITH sums AS (
  SELECT i.inbound_order_id AS id, COALESCE(SUM(i.quantity), 0)::int AS ordered
  FROM inbound_order_items i
  GROUP BY i.inbound_order_id
)
UPDATE inbound_orders o
SET total_ordered = s.ordered
FROM sums s
WHERE o.id = s.id;

-- 4) Si ya existen órdenes marcadas como RECEIVED, asumimos recibido = pedido
UPDATE inbound_orders
SET total_received = total_ordered
WHERE status IN ('RECEIVED');

-- 5) Checks sanos y un índice útil
ALTER TABLE inbound_orders
  ADD CONSTRAINT inbound_orders_total_nonneg CHECK (total_ordered >= 0 AND total_received >= 0) NOT VALID;

-- Opcional: valida el check en segundo plano si tu base es grande
-- ALTER TABLE inbound_orders VALIDATE CONSTRAINT inbound_orders_total_nonneg;

CREATE INDEX IF NOT EXISTS idx_inbound_orders_status ON inbound_orders(status);

COMMIT;
