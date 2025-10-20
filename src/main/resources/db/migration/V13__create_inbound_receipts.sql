-- V13__create_inbound_receipts.sql
-- Recibos de entrada (InboundReceipt) y sus ítems (InboundReceiptItem) con snapshot de producto
-- y campos para conciliar lo PEDIDO vs RECIBIDO.

BEGIN;

-- =========================
-- Tabla: inbound_receipts
-- =========================
CREATE TABLE IF NOT EXISTS inbound_receipts (
  id                BIGSERIAL PRIMARY KEY,
  receipt_number    VARCHAR(50) NOT NULL UNIQUE,            -- consecutivo legible
  inbound_order_id  BIGINT NOT NULL,                        -- FK a la orden
  warehouse_id      BIGINT NOT NULL,                        -- bodega donde se recibe
  total_received    INTEGER NOT NULL DEFAULT 0,             -- suma de items recibidos
  note              TEXT,
  created_by        VARCHAR(100),
  created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

ALTER TABLE inbound_receipts
  ADD CONSTRAINT fk_inb_receipt_order
    FOREIGN KEY (inbound_order_id) REFERENCES inbound_orders(id) ON DELETE RESTRICT,
  ADD CONSTRAINT fk_inb_receipt_wh
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE RESTRICT;

CREATE INDEX IF NOT EXISTS idx_inb_receipt_order ON inbound_receipts(inbound_order_id);
CREATE INDEX IF NOT EXISTS idx_inb_receipt_wh    ON inbound_receipts(warehouse_id);

ALTER TABLE inbound_receipts
  ADD CONSTRAINT chk_inb_receipt_total_nonneg CHECK (total_received >= 0) NOT VALID;
-- ALTER TABLE inbound_receipts VALIDATE CONSTRAINT chk_inb_receipt_total_nonneg;


-- ==============================
-- Tabla: inbound_receipt_items
-- ==============================
CREATE TABLE IF NOT EXISTS inbound_receipt_items (
  id                   BIGSERIAL PRIMARY KEY,
  inbound_receipt_id   BIGINT NOT NULL,                     -- FK al recibo
  product_id           BIGINT NOT NULL,                     -- referencia al producto
  product_sku          VARCHAR(32) NOT NULL,                -- snapshot SKU
  product_name         VARCHAR(255) NOT NULL,               -- snapshot nombre
  inbound_order_item_id BIGINT,                             -- (opcional) enlaza renglón de la orden
  expected_qty         INTEGER NOT NULL DEFAULT 0,          -- cantidad pedida (de la orden)
  quantity_received    INTEGER NOT NULL DEFAULT 0,          -- cantidad realmente recibida (editable)
  unit_cost            NUMERIC(19,2),                       -- opcional
  total_cost           NUMERIC(19,2),                       -- opcional
  status               VARCHAR(16) NOT NULL DEFAULT 'PENDING', -- PENDING|PARTIAL|COMPLETE|OVER|CANCELLED
  note                 TEXT
);

ALTER TABLE inbound_receipt_items
  ADD CONSTRAINT fk_inb_receipt_item_receipt
    FOREIGN KEY (inbound_receipt_id) REFERENCES inbound_receipts(id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_inb_receipt_item_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
  ADD CONSTRAINT fk_inb_receipt_item_order_item
    FOREIGN KEY (inbound_order_item_id) REFERENCES inbound_order_items(id) ON DELETE SET NULL;

-- Un producto una vez por recibo (simplifica UI/conciliación)
CREATE UNIQUE INDEX IF NOT EXISTS uq_inb_receipt_items_prod_per_receipt
  ON inbound_receipt_items(inbound_receipt_id, product_id);

CREATE INDEX IF NOT EXISTS idx_inb_receipt_items_receipt ON inbound_receipt_items(inbound_receipt_id);
CREATE INDEX IF NOT EXISTS idx_inb_receipt_items_product ON inbound_receipt_items(product_id);

-- Validaciones
ALTER TABLE inbound_receipt_items
  ADD CONSTRAINT chk_inb_receipt_qty_nonneg CHECK (quantity_received >= 0 AND expected_qty >= 0) NOT VALID;

ALTER TABLE inbound_receipt_items
  ADD CONSTRAINT chk_inb_receipt_status CHECK (status IN ('PENDING','PARTIAL','COMPLETE','OVER','CANCELLED')) NOT VALID;

-- (opcional) valida después en ventanas de mantenimiento
-- ALTER TABLE inbound_receipt_items VALIDATE CONSTRAINT chk_inb_receipt_qty_nonneg;
-- ALTER TABLE inbound_receipt_items VALIDATE CONSTRAINT chk_inb_receipt_status;

COMMIT;
