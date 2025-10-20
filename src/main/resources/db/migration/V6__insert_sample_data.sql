-- V6__insert_sample_data.sql
-- Semilla profesional: 7 proveedores, 5 categorías, 20 productos (COP)
-- Idempotente: evita duplicados si ya existen filas con el mismo name

------------------------------------------------------------
-- 1) PROVEEDORES
------------------------------------------------------------
INSERT INTO suppliers (name, contact_person, email, phone, address, created_at, updated_at)
SELECT 'Corsair', 'Ventas Latam', 'ventas@corsair.com', '+1-800-123-4567', 'Costa Mesa, CA, USA', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name='Corsair' AND email='ventas@corsair.com');

INSERT INTO suppliers (name, contact_person, email, phone, address, created_at, updated_at)
SELECT 'Logitech', 'Canales B2B', 'sales@logitech.com', '+1-800-234-5678', 'Lausana, Suiza', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name='Logitech' AND email='sales@logitech.com');

INSERT INTO suppliers (name, contact_person, email, phone, address, created_at, updated_at)
SELECT 'Samsung', 'Channel Partner', 'partners@samsung.com', '+82-2-1234-5678', 'Seúl, Corea', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name='Samsung' AND email='partners@samsung.com');

INSERT INTO suppliers (name, contact_person, email, phone, address, created_at, updated_at)
SELECT 'LG Electronics', 'B2B LATAM', 'b2b@lge.com', '+82-2-8765-4321', 'Seúl, Corea', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name='LG Electronics' AND email='b2b@lge.com');

INSERT INTO suppliers (name, contact_person, email, phone, address, created_at, updated_at)
SELECT 'Seagate', 'Distribución', 'channel@seagate.com', '+1-408-555-7890', 'Fremont, CA, USA', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name='Seagate' AND email='channel@seagate.com');

INSERT INTO suppliers (name, contact_person, email, phone, address, created_at, updated_at)
SELECT 'Kingston', 'Distribución', 'distribution@kingston.com', '+1-714-123-9000', 'Fountain Valley, CA, USA', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name='Kingston' AND email='distribution@kingston.com');

INSERT INTO suppliers (name, contact_person, email, phone, address, created_at, updated_at)
SELECT 'Asus', 'Global Channel', 'global@asus.com', '+886-2-2894-3447', 'Taipéi, Taiwán', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name='Asus' AND email='global@asus.com');

------------------------------------------------------------
-- 2) CATEGORÍAS
------------------------------------------------------------
INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Periféricos', 'Teclados, ratones y accesorios de entrada', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Periféricos');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Monitores', 'Pantallas y monitores profesionales', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Monitores');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Componentes', 'Memorias, discos y hardware interno', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Componentes');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Almacenamiento', 'Discos duros, SSD y unidades externas', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Almacenamiento');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Networking', 'Routers, switches y dispositivos de red', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Networking');

------------------------------------------------------------
-- 3) PRODUCTOS
-- FK por nombre: (SELECT id FROM categories WHERE name=...) / (SELECT id FROM suppliers WHERE name=...)
-- Cada insert evita duplicados por name.
------------------------------------------------------------
INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Teclado Corsair K70 RGB', 'Teclado mecánico para gaming con retroiluminación RGB', 550000, 50,
       (SELECT id FROM categories WHERE name='Periféricos'),
       (SELECT id FROM suppliers WHERE name='Corsair'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Teclado Corsair K70 RGB');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Mouse Logitech G Pro X', 'Mouse gamer inalámbrico de alto rendimiento', 480000, 70,
       (SELECT id FROM categories WHERE name='Periféricos'),
       (SELECT id FROM suppliers WHERE name='Logitech'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Mouse Logitech G Pro X');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Auriculares Logitech G733', 'Auriculares inalámbricos con sonido envolvente', 520000, 40,
       (SELECT id FROM categories WHERE name='Periféricos'),
       (SELECT id FROM suppliers WHERE name='Logitech'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Auriculares Logitech G733');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Monitor Samsung Odyssey G5', 'Monitor curvo QHD 32" 144Hz', 1800000, 25,
       (SELECT id FROM categories WHERE name='Monitores'),
       (SELECT id FROM suppliers WHERE name='Samsung'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Monitor Samsung Odyssey G5');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Monitor LG UltraWide 29"', 'Monitor ultrawide IPS 29"', 1300000, 30,
       (SELECT id FROM categories WHERE name='Monitores'),
       (SELECT id FROM suppliers WHERE name='LG Electronics'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Monitor LG UltraWide 29"');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'SSD Samsung 980 Pro 1TB', 'Unidad de estado sólido NVMe PCIe 4.0', 720000, 100,
       (SELECT id FROM categories WHERE name='Componentes'),
       (SELECT id FROM suppliers WHERE name='Samsung'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='SSD Samsung 980 Pro 1TB');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Memoria Kingston Fury 16GB', 'Módulo DDR4 3200MHz alto rendimiento', 280000, 150,
       (SELECT id FROM categories WHERE name='Componentes'),
       (SELECT id FROM suppliers WHERE name='Kingston'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Memoria Kingston Fury 16GB');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Disco Duro Seagate BarraCuda 2TB', 'Disco duro interno SATA 7200rpm', 250000, 200,
       (SELECT id FROM categories WHERE name='Almacenamiento'),
       (SELECT id FROM suppliers WHERE name='Seagate'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Disco Duro Seagate BarraCuda 2TB');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Router Asus RT-AX88U', 'Router Wi-Fi 6 de alto rendimiento', 1200000, 15,
       (SELECT id FROM categories WHERE name='Networking'),
       (SELECT id FROM suppliers WHERE name='Asus'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Router Asus RT-AX88U');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Adaptador de Red Asus XG-C100C', 'Adaptador 10GbE PCIe', 450000, 40,
       (SELECT id FROM categories WHERE name='Networking'),
       (SELECT id FROM suppliers WHERE name='Asus'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Adaptador de Red Asus XG-C100C');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Mouse Corsair Dark Core RGB', 'Mouse inalámbrico con sensor de precisión', 400000, 60,
       (SELECT id FROM categories WHERE name='Periféricos'),
       (SELECT id FROM suppliers WHERE name='Corsair'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Mouse Corsair Dark Core RGB');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Auriculares Corsair HS80', 'Headset gamer con sonido espacial', 480000, 50,
       (SELECT id FROM categories WHERE name='Periféricos'),
       (SELECT id FROM suppliers WHERE name='Corsair'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Auriculares Corsair HS80');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Monitor Asus ProArt 27"', 'Monitor profesional para diseño gráfico', 1900000, 20,
       (SELECT id FROM categories WHERE name='Monitores'),
       (SELECT id FROM suppliers WHERE name='Asus'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Monitor Asus ProArt 27"');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'SSD Kingston KC3000 2TB', 'SSD NVMe PCIe 4.0 alto rendimiento', 1000000, 80,
       (SELECT id FROM categories WHERE name='Componentes'),
       (SELECT id FROM suppliers WHERE name='Kingston'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='SSD Kingston KC3000 2TB');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'HDD Externo Seagate Expansion 4TB', 'Unidad portátil de alta capacidad', 400000, 120,
       (SELECT id FROM categories WHERE name='Almacenamiento'),
       (SELECT id FROM suppliers WHERE name='Seagate'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='HDD Externo Seagate Expansion 4TB');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'SSD Externo Samsung T7 1TB', 'Unidad portátil ultrarrápida USB-C', 600000, 90,
       (SELECT id FROM categories WHERE name='Almacenamiento'),
       (SELECT id FROM suppliers WHERE name='Samsung'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='SSD Externo Samsung T7 1TB');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Switch Gigabit Asus 8 Puertos', 'Switch Ethernet Gigabit metálico 8 puertos', 180000, 75,
       (SELECT id FROM categories WHERE name='Networking'),
       (SELECT id FROM suppliers WHERE name='Asus'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Switch Gigabit Asus 8 Puertos');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Memoria Corsair Vengeance 32GB DDR5', 'Kit DDR5 5600MHz para entusiastas', 1100000, 60,
       (SELECT id FROM categories WHERE name='Componentes'),
       (SELECT id FROM suppliers WHERE name='Corsair'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Memoria Corsair Vengeance 32GB DDR5');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Monitor LG 4K UHD 32"', 'Monitor IPS UHD HDR10', 2100000, 15,
       (SELECT id FROM categories WHERE name='Monitores'),
       (SELECT id FROM suppliers WHERE name='LG Electronics'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Monitor LG 4K UHD 32"');

INSERT INTO products (name, description, price, stock, category_id, supplier_id, created_at, updated_at)
SELECT 'Router Asus ROG Rapture GT-AX11000', 'Router gaming Wi-Fi 6 tri-banda', 2000000, 10,
       (SELECT id FROM categories WHERE name='Networking'),
       (SELECT id FROM suppliers WHERE name='Asus'),
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Router Asus ROG Rapture GT-AX11000');

