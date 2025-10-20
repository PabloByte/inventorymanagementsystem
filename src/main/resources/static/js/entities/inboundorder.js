// Entidad pura + DTOs (sin fetch)

export class InboundOrderItem {
  constructor({ productId = null, productName = null, quantity = 0 } = {}) {
    this.productId = productId != null ? Number(productId) : null;
    this.productName = productName ?? null;
    this.quantity = Number(quantity) || 0;
  }

  static fromDto(dto = {}) {
    return new InboundOrderItem({
      productId: dto.productId ?? dto.id ?? null,
      productName: dto.productName ?? dto.name ?? null,
      quantity: dto.quantity ?? 0,
    });
  }

  toInsertDto() {
    return {
      productId: this.productId != null ? Number(this.productId) : null,
      quantity: Number(this.quantity) || 0,
    };
  }
}

export default class InboundOrder {
  constructor({ supplierId = null, status = "PENDING", warehouseId = 1, items = [] } = {}) {
    this.supplierId = supplierId != null ? Number(supplierId) : null;
    this.status = String(status || "PENDING").toUpperCase();
    this.warehouseId = warehouseId != null ? Number(warehouseId) : null;
    this.items = Array.isArray(items)
      ? items.map(it => (it instanceof InboundOrderItem ? it : new InboundOrderItem(it)))
      : [];
  }

  toInsertDto() {
    return {
      supplierId: this.supplierId,
      status: this.status,
      warehouseId: this.warehouseId,
      items: this.items.map(it => it.toInsertDto()),
    };
  }

  addItem(item) {
    const it = item instanceof InboundOrderItem ? item : new InboundOrderItem(item);
    if (it.productId == null || it.quantity <= 0) {
      throw new Error("Item inválido: productId y quantity > 0 son requeridos.");
    }
    this.items.push(it);
  }

  removeItemAt(index) { if (index >= 0 && index < this.items.length) this.items.splice(index, 1); }
  clearItems() { this.items.length = 0; }
}


