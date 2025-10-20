-- Migración para agregar campos adicionales a la tabla products
-- Campos: serial, lote, dimensiones/peso, estado certificado, observación
 
-- Agregar columna serial (opcional, puede ser null)
ALTER TABLE products 
ADD COLUMN serial VARCHAR(100);
 
-- Agregar columna lote (opcional, puede ser null)
ALTER TABLE products 
ADD COLUMN lote VARCHAR(50);
 
-- Agregar columna dimensiones (opcional, puede ser null)
-- Almacenamos como texto para flexibilidad (ej: "10x20x30 cm")
ALTER TABLE products 
ADD COLUMN dimensiones VARCHAR(100);
 
-- Agregar columna peso (opcional, puede ser null)
-- Usamos DECIMAL para precisión en peso
ALTER TABLE products 
ADD COLUMN peso DECIMAL(10,3);
 
-- Agregar columna estado_certificado (opcional, puede ser null)
-- Usamos ENUM para estados predefinidos
ALTER TABLE products 
ADD COLUMN estado_certificado VARCHAR(20);
 
-- Agregar columna observacion (opcional, puede ser null)
-- Texto largo para observaciones detalladas
ALTER TABLE products 
ADD COLUMN observacion TEXT;
 
-- Crear índices para mejorar consultas
CREATE INDEX idx_products_serial ON products(serial) WHERE serial IS NOT NULL;
CREATE INDEX idx_products_lote ON products(lote) WHERE lote IS NOT NULL;
CREATE INDEX idx_products_estado_certificado ON products(estado_certificado) WHERE estado_certificado IS NOT NULL;
 
-- Agregar comentarios a las columnas para documentación
COMMENT ON COLUMN products.serial IS 'Número de serie del producto';
COMMENT ON COLUMN products.lote IS 'Número de lote del producto';
COMMENT ON COLUMN products.dimensiones IS 'Dimensiones del producto (ej: 10x20x30 cm)';
COMMENT ON COLUMN products.peso IS 'Peso del producto en kilogramos';
COMMENT ON COLUMN products.estado_certificado IS 'Estado de certificación del producto';
COMMENT ON COLUMN products.observacion IS 'Observaciones adicionales sobre el producto';
 
-- Crear constraint para validar valores de estado_certificado
ALTER TABLE products 
ADD CONSTRAINT chk_estado_certificado 
CHECK (estado_certificado IN ('CERTIFICADO', 'NO_CERTIFICADO', 'EN_PROCESO', 'VENCIDO') OR estado_certificado IS NULL);
 
-- Crear constraint para validar peso positivo
ALTER TABLE products 
ADD CONSTRAINT chk_peso_positivo 
CHECK (peso IS NULL OR peso > 0);