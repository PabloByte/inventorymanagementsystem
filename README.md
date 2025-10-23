# Inventory Management System — Spring Boot 3 (Java 17)

[![CI](https://github.com/PabloByte/inventorymanagementsystem/actions/workflows/ci.yml/badge.svg)]
[(https://github.com/PabloByte/inventorymanagementsystem/actions/workflows/ci.yml)]
![Java 17](https://img.shields.io/badge/Java-17-red)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

API de gestión de inventarios con **Swagger** y **Actuator**, migraciones con **Flyway** y generación de **PDF** para órdenes/recibos.

> **Nota demo (plan gratis):** el primer request puede demorar por *cold start*. Verifique estado en **/actuator/health** (`UP`).

## 🚀 Demo
- **API base:** https://inventorymanagementsystem-w4nu.onrender.com
- **Swagger:** https://inventorymanagementsystem-w4nu.onrender.com/swagger-ui.html
- **Health:** https://inventorymanagementsystembackend-to9c.onrender.com/actuator/health

---

## 📌 Estado actual (16/10/2025)
- **PRODUCTOS**
  - Filtro por prioridad para reposición
  - Pedido menor a stock personalizado
  - Exportar CSV
  - Listar / Crear / Editar  
  - **No se implementa borrar** (para preservar historial)

- **CATEGORÍA**
  - Filtrar por categoría con detalle de productos (id, nombre, descripción, precio, stock, proveedor, serial, lote, estadoCertificado, observación)
  - Crear / Actualizar  
  - **No eliminar** (no afectar inventario)

- **ÓRDENES ENTRANTES (InboundOrder)**
  - Crear orden (proveedor, bodega, estado inicial `PENDING`)
  - Selección de productos con **detalle enriquecido** (nombres similares, atributos diferenciadores)
  - **PDF**: id, número de orden, proveedor, estado, bodega, fecha, código/producto, cantidad, costo unitario, total, serial, lote, dimensiones, peso, estadoCertificado, observaciones

- **RECIBIR ORDEN ENTRANTE (InboundReceipt)**
  - Cargar orden, comparar pedido vs recibido
  - Nota de recepción
  - Calcula **pendientes**
  - **PDF** de recibo

- **CANCELAR ORDEN ENTRANTE**
  - Cancela sin afectar inventario

- **SUPPLIER**
  - Crear proveedor (pendiente CRUD completo + NIT vía Flyway)

- **STOCK**: autogestionado  
- **INVENTORY LEDGER**: autogestionado

- **WAREHOUSE**
  - **Pendiente** CRUD completo

- **OUTBOUND ORDER**
  - **Pendiente** CRUD + PDF (cliente/destino, entre bodegas)

- **SEGURIDAD**
  - **En implementación:** Spring Security (JWT, rol “encargado”)

---

## 🧠 Qué demuestra este proyecto
- Diseño de **API REST** documentada con **Swagger/OpenAPI**
- Observabilidad mínima: **Actuator** (`/health`) y *build info* (por perfiles)
- Persistencia con **JPA/Hibernate** + **Flyway**
- Generación de **PDF** (Apache PDFBox) para flujos operativos
- Perfiles configurados: `local` y `supabase` (demo en nube)
- **CI** con GitHub Actions (build en cada push/PR)

---

## 🧱 Stack
Java 17 • Spring Boot 3 • Spring Web • Spring Data JPA • PostgreSQL/MySQL • Flyway • MapStruct • Apache PDFBox • Actuator • OpenAPI (springdoc)

---

## ▶️ Ejecutar en local
1. Configura DB en `application-local.yml`.  
2. Arranca con el perfil `local`:
   ```bash
   mvn clean spring-boot:run -Dspring.profiles.active=local

