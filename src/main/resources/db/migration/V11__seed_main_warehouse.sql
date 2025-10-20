-- V10__seed_main_warehouse.sql  (PostgreSQL)
INSERT INTO warehouses (code, name, address, active, created_at, updated_at)
VALUES ('MAIN', 'Bodega Principal', 'N/A', true, NOW(), NOW())
ON CONFLICT (code) DO NOTHING;