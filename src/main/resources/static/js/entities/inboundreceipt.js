// Entidades de UI para el flujo de recepción

export class InboundReceiptLine {
  constructor({
    productId,
    sku = "",
    name = "",
    orderedQty = 0,        // cantidad pedida total para ese producto
    receivedSoFar = 0,     // cantidad recibida acumulada (antes de este recibo)
    unitCost = 0,          // costo unitario sugerido
    note = ""
  } = {}) {
    this.productId = Number(productId);
    this.sku = sku || "";
    this.name = name || "";
    this.orderedQty = Number(orderedQty) || 0;
    this.receivedSoFar = Number(receivedSoFar) || 0;

    // editable por el usuario
    this.receiveQty = 0;                         // cantidad a recibir "ahora"
    this.unitCost = Number(unitCost) || 0;       // editable
    this.note = note || "";
  }

  get expectedQty() {
    // pendiente antes de este recibo
    return Math.max(0, this.orderedQty - this.receivedSoFar);
  }

  get lineTotal() {
    return Number(this.receiveQty || 0) * Number(this.unitCost || 0);
  }

  toReceiveDto() {
    // mapea a InboundOrderReceiveItemDto
    return {
      productId: this.productId,
      receivedQty: Number(this.receiveQty || 0),
      unitCost: Number(this.unitCost || 0),
      note: this.note || null
    };
  }
}

export default class InboundReceiptDraft {
  constructor({ orderNumber, receivedBy = "", note = "", items = [] } = {}) {
    this.orderNumber = orderNumber || "";
    this.receivedBy = receivedBy || "";
    this.note = note || "";
    /** @type {InboundReceiptLine[]} */
    this.items = Array.isArray(items) ? items : [];
  }

  addLine(line) {
    const l = line instanceof InboundReceiptLine ? line : new InboundReceiptLine(line);
    this.items.push(l);
  }

  clear() { this.items.length = 0; }

  // Totales live (UI)
  get totalLines() { return this.items.filter(l => Number(l.receiveQty) > 0).length; }
  get totalQty()   { return this.items.reduce((s,l)=> s + (Number(l.receiveQty)||0), 0); }
  get totalCost()  { return this.items.reduce((s,l)=> s + (Number(l.receiveQty||0)*Number(l.unitCost||0)), 0); }

  // Para POST /{orderNumber}/receive (InboundOrderReceiveDto)
  toReceiveBody() {
    return {
      items: this.items
        .filter(l => Number(l.receiveQty) > 0) // solo líneas con qty > 0
        .map(l => l.toReceiveDto()),
      note: this.note || null,
      receivedBy: (this.receivedBy && this.receivedBy.trim()) ? this.receivedBy.trim() : null
    };
  }
}

/* ====== RESULTADO DEL RECIBO (para manejar pdfUrl sin tocar lo existente) ====== */

export class InboundReceiptResult {
  constructor({
    receiptId = null,
    receiptNumber = null,
    orderNumber = null,
    totalReceived = 0,
    createdAt = null,
    pdfUrl = null
  } = {}) {
    this.receiptId = receiptId;
    this.receiptNumber = receiptNumber;
    this.orderNumber = orderNumber;
    this.totalReceived = totalReceived;
    this.createdAt = createdAt;
    this.pdfUrl = pdfUrl; // puede venir del backend; si no, se construye con helpers del service
  }

  static fromDto(dto = {}) {
    return new InboundReceiptResult({
      receiptId: dto.receiptId ?? dto.id ?? null,
      receiptNumber: dto.receiptNumber ?? null,
      orderNumber: dto.orderNumber ?? null,
      totalReceived: dto.totalReceived ?? 0,
      createdAt: dto.createdAt ?? null,
      pdfUrl: dto.pdfUrl ?? null
    });
  }
}

