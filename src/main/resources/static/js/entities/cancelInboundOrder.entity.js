// /js/cancelInboundOrder.entity.js
// Modelos inmutables para Pending y Preview de InboundOrder

// --- Utilidades locales ------------------------------------------------------
const asString = (v, d = "") => (v == null ? d : String(v));
const asInt = (v, d = 0) => (Number.isFinite(v) ? v : d);
const asNum = (v, d = 0) => (v == null ? d : v); // BigDecimal llega como número o string según backend

const freeze = (obj) => Object.freeze(obj);

// --- Reglas de negocio mínimas ----------------------------------------------
const isCancellable = (status) => asString(status).toUpperCase() === "PENDING";

// --- Entity: referencia para selector de pendientes --------------------------
export function PendingInboundOrderRef(dto) {
  const model = {
    // 👇 IMPORTANTE: mantener 'id' para que el pages pueda pedir preview/cancel
    id: dto?.id,
    // Puedes usar este orderNumber como "Código de la orden" en la UI si quieres
    orderNumber: asString(dto?.orderNumber),
    supplierName: asString(dto?.supplierName),
    warehouse: freeze({
      name: asString(dto?.warehouseName),
      code: asString(dto?.warehouseCode),
    }),
  };
  return freeze(model);
}

// --- Entity: previsualización completa --------------------------------------
export function InboundOrderPreview(dto) {
  const items = Array.isArray(dto?.items)
    ? dto.items.map((it) =>
        freeze({
          // Para la tabla, etiqueta en la UI este 'sku' como "Código" del producto
          sku: asString(it?.sku),
          name: asString(it?.name),
          uom: it?.uom ?? null,
          orderedQty: asInt(it?.orderedQty, 0),
          unitPrice: it?.unitPrice ?? null,
          subtotal: it?.subtotal ?? null,
        })
      )
    : [];

  const totals = freeze({
    lines: asInt(dto?.totals?.lines, items.length),
    units: asInt(
      dto?.totals?.units,
      items.reduce((a, b) => a + (b.orderedQty || 0), 0)
    ),
    subtotal: asNum(dto?.totals?.subtotal, 0),
    tax: asNum(dto?.totals?.tax, 0),
    grandTotal: asNum(
      dto?.totals?.grandTotal,
      asNum(dto?.totals?.subtotal, 0)
    ),
  });

  const model = {
    id: dto?.id,
    orderNumber: asString(dto?.orderNumber),
    status: asString(dto?.status),
    createdAt: dto?.createdAt ?? null,

    supplier: freeze({
      name: asString(dto?.supplier?.name),
      nit: dto?.supplier?.nit ?? null,
    }),

    warehouse: freeze({
      name: asString(dto?.warehouse?.name),
      code: asString(dto?.warehouse?.code),
    }),

    items: freeze(items),
    totals,

    cancelledAt: dto?.cancelledAt ?? null,
    cancelReason: dto?.cancelReason ?? null,
    cancelledBy: dto?.cancelledBy ?? null,

    // Derivados/cómodos para la UI:
    get isCancellable() {
      return isCancellable(model.status);
    },
    get isCancelled() {
      return asString(model.status).toUpperCase() === "CANCELLED";
    },
  };

  return freeze(model);
}

