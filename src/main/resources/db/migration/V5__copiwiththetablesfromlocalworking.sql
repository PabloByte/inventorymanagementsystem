-- ===========================================
-- V5__recrear_core_inventario.sql
-- Congelado de la estructura del inventario
-- ===========================================

-- Tabla categories
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Tabla suppliers
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(100),
    address VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Tabla products
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    price NUMERIC(19,2) NOT NULL,
    stock INT NOT NULL,
    category_id BIGINT,
    supplier_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- Índices útiles para products
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_supplier ON products(supplier_id);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);

-- Tabla inbound_orders
CREATE TABLE IF NOT EXISTS inbound_orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    pdf_path VARCHAR(255),
    status VARCHAR(255) NOT NULL CHECK (status IN ('PENDING','RECEIVED','CANCELLED')),
    total_cost NUMERIC(38,2) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    supplier_id BIGINT NOT NULL REFERENCES suppliers(id)
);

-- Tabla inbound_order_items
CREATE TABLE IF NOT EXISTS inbound_order_items (
    id BIGSERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    unit_cost NUMERIC(38,2) NOT NULL,
    total_cost NUMERIC(38,2) NOT NULL,
    inbound_order_id BIGINT NOT NULL REFERENCES inbound_orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id)
);
