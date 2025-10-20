// /js/entities/Product.js
export default class Product {
  
  constructor({
    id = null,
    sku = "",
    barcode = null,
    name = "",
    description = "",
    price = 0,
    stock = 0,
    reorderPoint = 5,
    status = "ACTIVE", // "ACTIVE" | "INACTIVE"
    categoryId = null,
    categoryName = null,
    supplierId = null,
    supplierName = null,
    createdAt = null,
    updatedAt = null,

    // --- Nuevos campos alineados con la BD ---
    serial = null,                 // products.serial (VARCHAR(100))
    lote = null,                   // products.lote (VARCHAR(50))
    dimensiones = null,            // products.dimensiones (VARCHAR(100), ej "10x20x30 cm")
    peso = null,                   // products.peso (DECIMAL(10,3)) -> número o null
    estadoCertificado = null,      // products.estado_certificado (ENUM lógico via CHECK): 'CERTIFICADO' | 'NO_CERTIFICADO' | 'EN_PROCESO' | 'VENCIDO'
    observacion = null,            // products.observacion (TEXT)
  } = {}) {
    this.id = id;
    this.sku = sku;
    this.barcode = barcode;
    this.name = name;
    this.description = description;
    this.price = Number(price) || 0;
    this.stock = Number(stock) || 0;
    this.reorderPoint = Number(reorderPoint) || 0;
    this.status = status;
    this.categoryId = categoryId;
    this.categoryName = categoryName;
    this.supplierId = supplierId;
    this.supplierName = supplierName;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;

    // Nuevos campos
    this.serial = (serial ?? null);
    this.lote = (lote ?? null);
    this.dimensiones = (dimensiones ?? null);
    this.peso = (peso !== null && peso !== undefined && peso !== "" ? Number(peso) : null);
    this.estadoCertificado = (estadoCertificado ?? null);
    this.observacion = (observacion ?? null);
  }

  // Del DTO del backend -> objeto Product para UI
  static fromDto(dto = {}) {
    return new Product({
      id: dto.id,
      sku: dto.sku,
      barcode: dto.barcode ?? null,
      name: dto.name,
      description: dto.description ?? "",
      price: dto.price,
      stock: dto.stock,
      reorderPoint: dto.reorderPoint,
      status: dto.status, // "ACTIVE"/"INACTIVE"
      categoryId: dto.categoryId ?? null,
      categoryName: dto.categoryName ?? null,
      supplierId: dto.supplierId ?? null,
      supplierName: dto.supplierName ?? null,
      createdAt: dto.createdAt ?? null,
      updatedAt: dto.updatedAt ?? null,

      // nuevos campos (nombres iguales a las propiedades del DTO)
      serial: dto.serial ?? null,
      lote: dto.lote ?? null,
      dimensiones: dto.dimensiones ?? null,
      peso: (dto.peso !== null && dto.peso !== undefined && dto.peso !== "" ? Number(dto.peso) : null),
      estadoCertificado: dto.estadoCertificado ?? null,
      observacion: dto.observacion ?? null,
    });
  }

  // Objeto UI -> DTO para create/update en backend
  toInsertDto() {
    return {
      sku: (this.sku || "").trim().toUpperCase(),
      barcode: (this.barcode || "").trim() || null,
      name: (this.name || "").trim(),
      description: (this.description || "").trim(),
      price: Number(this.price),
      reorderPoint: Number(this.reorderPoint),
      status: String(this.status || "ACTIVE").toUpperCase(),
      categoryId: this.categoryId != null ? Number(this.categoryId) : null,
      supplierId: this.supplierId != null ? Number(this.supplierId) : null,

      // nuevos campos -> mismos nombres que espera el backend
      serial: (this.serial ?? null) && String(this.serial).trim() !== "" ? String(this.serial).trim() : null,
      lote: (this.lote ?? null) && String(this.lote).trim() !== "" ? String(this.lote).trim() : null,
      dimensiones: (this.dimensiones ?? null) && String(this.dimensiones).trim() !== "" ? String(this.dimensiones).trim() : null,
      peso: (this.peso !== null && this.peso !== undefined && this.peso !== "") ? Number(this.peso) : null,
      estadoCertificado: (this.estadoCertificado ?? null) && String(this.estadoCertificado).trim() !== "" 
        ? String(this.estadoCertificado).toUpperCase().trim() 
        : null, // 'CERTIFICADO' | 'NO_CERTIFICADO' | 'EN_PROCESO' | 'VENCIDO' | null
      observacion: (this.observacion ?? null) && String(this.observacion).trim() !== "" ? String(this.observacion).trim() : null,
    };
  }



// Añádelo dentro de la clase Product
toJSON() {
  return {
    id: this.id,
    name: this.name,
    description: this.description,
    price: this.price,
    stock: this.stock,
    supplierName: this.supplierName,
    serial: this.serial,
    lote: this.lote,
    estadoCertificado: this.estadoCertificado,
    observacion: this.observacion,
    // (opcional, por si algún día quieres mostrarlos)
    sku: this.sku,
    barcode: this.barcode,
  };
}










}
