-- V10__create_stock.sql
-- Fuente de verdad del inventario por (producto, bodega)

-- Asegura función de updated_at (si no existe)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'set_updated_at') THEN
    CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $f$
    BEGIN
      NEW.updated_at := NOW();
      RETURN NEW;
    END;
    $f$ LANGUAGE plpgsql;
  END IF;
END$$;

CREATE TABLE IF NOT EXISTS stock (
  id            BIGSERIAL PRIMARY KEY,

  product_id    BIGINT NOT NULL,
  warehouse_id  BIGINT NOT NULL,

  quantity      INTEGER NOT NULL DEFAULT 0,         -- saldo en la bodega (no negativo)
  created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

  -- Para JPA @Version (optimistic locking) si quieres usarlo a nivel app
  version       INTEGER NOT NULL DEFAULT 0,

  CONSTRAINT fk_stock_product   FOREIGN KEY (product_id)   REFERENCES products(id),
  CONSTRAINT fk_stock_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),

  CONSTRAINT uq_stock_product_warehouse UNIQUE (product_id, warehouse_id),
  CONSTRAINT ck_stock_quantity_nonneg CHECK (quantity >= 0)
);

-- Índices útiles
CREATE INDEX IF NOT EXISTS idx_stock_product   ON stock (product_id);
CREATE INDEX IF NOT EXISTS idx_stock_warehouse ON stock (warehouse_id);

-- Trigger para mantener updated_at
DROP TRIGGER IF EXISTS trg_stock_set_updated_at ON stock;
CREATE TRIGGER trg_stock_set_updated_at
BEFORE UPDATE ON stock
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
