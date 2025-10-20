-- V17__add_cancel_audit_to_inbound_orders.sql
-- Objetivo: agregar trazabilidad de cancelación a inbound_orders
-- Base de datos: PostgreSQL

ALTER TABLE inbound_orders
    ADD COLUMN IF NOT EXISTS cancel_reason  VARCHAR(255),
    ADD COLUMN IF NOT EXISTS cancelled_at   TIMESTAMP,
    ADD COLUMN IF NOT EXISTS cancelled_by   VARCHAR(100);








