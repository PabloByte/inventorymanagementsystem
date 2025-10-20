-- V8__create_warehouse.sql
-- Crea la tabla de bodegas (warehouses)

-- Función genérica para actualizar updated_at en UPDATE (si aún no existe)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_proc WHERE proname = 'set_updated_at'
  ) THEN
    CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $f$
    BEGIN
      NEW.updated_at := NOW();
      RETURN NEW;
    END;
    $f$ LANGUAGE plpgsql;
  END IF;
END$$;

-- Tabla principal
CREATE TABLE IF NOT EXISTS warehouses (
  id           BIGSERIAL PRIMARY KEY,
  code         VARCHAR(32)  NOT NULL,      -- ej: BOG-01, MAIN, MED-02
  name         VARCHAR(120) NOT NULL,      -- nombre legible
  address      VARCHAR(255),               -- opcional
  active       BOOLEAN      NOT NULL DEFAULT TRUE,

  created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

  CONSTRAINT uq_warehouse_code UNIQUE (code),
  CONSTRAINT ck_warehouse_code_format CHECK (code ~ '^[A-Z0-9._-]+$') -- simple formato seguro
);

-- Índices útiles
CREATE INDEX IF NOT EXISTS idx_warehouses_active ON warehouses (active);
CREATE INDEX IF NOT EXISTS idx_warehouses_name   ON warehouses (name);

-- Trigger para mantener updated_at
DROP TRIGGER IF EXISTS trg_warehouses_set_updated_at ON warehouses;
CREATE TRIGGER trg_warehouses_set_updated_at
BEFORE UPDATE ON warehouses
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
