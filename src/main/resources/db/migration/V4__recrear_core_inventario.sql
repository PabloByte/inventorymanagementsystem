
CREATE TABLE IF NOT EXISTS categories (
  id           BIGSERIAL PRIMARY KEY,
  name         VARCHAR(255) NOT NULL UNIQUE,
  description  VARCHAR(255),
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

CREATE TABLE IF NOT EXISTS suppliers (
  id              BIGSERIAL PRIMARY KEY,
  name            VARCHAR(255) NOT NULL,
  contact_person  VARCHAR(255),
  email           VARCHAR(255),
  phone           VARCHAR(100),
  address         VARCHAR(500),
  created_at      TIMESTAMP,
  updated_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
  id           BIGSERIAL PRIMARY KEY,
  name         VARCHAR(255) NOT NULL,
  description  VARCHAR(500),
  price        NUMERIC(19,2) NOT NULL,
  stock        INT NOT NULL,
  category_id  BIGINT,
  supplier_id  BIGINT,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP,
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
  CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- 2) Índices recomendados (idempotentes)
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_supplier ON products(supplier_id);
CREATE INDEX IF NOT EXISTS idx_products_name     ON products(name);

-- 3) Limpiar tabla singular (si quedó de intentos previos)
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS supplier CASCADE;