-- V9__product_enhancements.sql
-- Agrega sku, barcode, reorder_point, status a products y crea índices/constraints

-- 1) Nuevas columnas con defaults suaves
ALTER TABLE products
  ADD COLUMN IF NOT EXISTS sku VARCHAR(32),
  ADD COLUMN IF NOT EXISTS barcode VARCHAR(64),
  ADD COLUMN IF NOT EXISTS reorder_point INT,
  ADD COLUMN IF NOT EXISTS status VARCHAR(16);

-- 2) Backfill de datos existentes
--   - sku temporal basado en id
--   - reorder_point default 5
--   - status ACTIVE
UPDATE products
SET
  sku = COALESCE(sku, CONCAT('SKU-', LPAD(CAST(id AS TEXT), 6, '0'))),
  reorder_point = COALESCE(reorder_point, 5),
  status = COALESCE(status, 'ACTIVE');

-- 3) Constraints NOT NULL (después del backfill)
ALTER TABLE products
  ALTER COLUMN sku SET NOT NULL,
  ALTER COLUMN reorder_point SET NOT NULL,
  ALTER COLUMN status SET NOT NULL;

-- 4) Unique constraint para SKU (a nivel DB)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'uq_product_sku'
  ) THEN
    ALTER TABLE products
      ADD CONSTRAINT uq_product_sku UNIQUE (sku);
  END IF;
END$$;

-- 5) Índice único PARCIAL para barcode (solo cuando barcode IS NOT NULL)
--    JPA no soporta esto; se hace en SQL.
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_indexes
    WHERE schemaname = 'public'
      AND indexname = 'uq_product_barcode_not_null'
  ) THEN
    CREATE UNIQUE INDEX uq_product_barcode_not_null
      ON products (barcode)
      WHERE barcode IS NOT NULL;
  END IF;
END$$;

-- 6) Índices de apoyo para consultas frecuentes
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_product_name') THEN
    CREATE INDEX idx_product_name ON products (name);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_product_category') THEN
    CREATE INDEX idx_product_category ON products (category_id);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_product_supplier') THEN
    CREATE INDEX idx_product_supplier ON products (supplier_id);
  END IF;
END$$;

-- (Opcional futuro) Si usas búsquedas LIKE '%texto%', evalúa extensión pg_trgm + índice GIN.
