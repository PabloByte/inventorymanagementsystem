import { apiUrl } from "../config/env.js";

// http robusto (mismo patrón que products/inboundorder)
async function http(method, url, body) {
  const res = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: body ? JSON.stringify(body) : undefined,
  });

  let payload; let isJson = false;
  try { payload = await res.clone().json(); isJson = true; }
  catch { try { payload = await res.text(); } catch { payload = null; } }

  if (!res.ok) {
    let msg = `HTTP ${res.status}`;
    if (isJson && payload && typeof payload === "object") {
      if (payload.detail) msg = payload.detail;
      else if (payload.title) msg = payload.title;
      if (Array.isArray(payload.errors) && payload.errors.length) {
        const joined = payload.errors
          .map(e => (e.field ? `${e.field}: ${e.message || e.defaultMessage || "inválido"}` : (e.message || e.defaultMessage || "")))
          .filter(Boolean).join(" • ");
        if (joined) msg = joined;
      }
      if (payload.message && !payload.detail) msg = payload.message;
      if (payload.error && !payload.detail && !payload.message) msg = payload.error;
    } else if (typeof payload === "string" && payload.trim()) {
      const m = payload.match(/<title>(.*?)<\/title>/i);
      msg = m?.[1] || payload.slice(0, 280);
    }
    throw new Error(msg);
  }

  if (res.status === 204) return null;
  if (isJson) return payload;
  if (typeof payload === "string") { try { return JSON.parse(payload); } catch { return payload; } }
  return payload ?? null;
}

/* ====== API ====== */

// Lista de órdenes PENDING para el <select>
// Ajusta si tu backend usa otro path
export async function listPendingOrders() {
  return await http("GET", `${apiUrl}/inbound-orders/pending`);
}

// Detalle por orderNumber para armar la tabla editable
// Esperado mínimo:
// {
//   orderNumber, status, supplier, warehouse, warehouseId,
//   items:[ {productId, sku, name, orderedQty, receivedSoFar, unitCost} ]
// }
export async function getOrderDetail(orderNumber) {
  return await http("GET", `${apiUrl}/inbound-orders/${encodeURIComponent(orderNumber)}`);
}

// POST recibir y ajustar → devuelve InboundReceiptResponseDto
// Ideal: que el backend añada pdfDownloadUrl en la respuesta para descargar
export async function receiveAndAdjust(orderNumber, body /* InboundOrderReceiveDto */) {
  return await http("POST", `${apiUrl}/inbound-orders/${encodeURIComponent(orderNumber)}/receive`, body);
}

/* ====== Helpers PDF (agregar al final, sin modificar lo anterior) ====== */

/** Devuelve la URL del PDF del recibo.
 *  Acepta: el DTO de respuesta, un receiptId, o una url ya armada.
 */
export function getReceiptPdfUrl(receiptRef) {
  // Si ya es una URL absoluta/relativa
  if (typeof receiptRef === "string" && (receiptRef.startsWith("/") || receiptRef.startsWith("http"))) {
    return receiptRef;
  }

  // Si es un objeto DTO que trae pdfUrl
  if (receiptRef && typeof receiptRef === "object") {
    if (receiptRef.pdfUrl) return receiptRef.pdfUrl;
    const id = receiptRef.receiptId ?? receiptRef.id;
    if (id != null) return `${apiUrl}/inbound-receipts/${id}/pdf`;
  }

  // Si es un número/id
  if (Number.isFinite(receiptRef)) {
    return `${apiUrl}/inbound-receipts/${receiptRef}/pdf`;
  }

  return null;
}

/** Abre el PDF en una pestaña (inline en el visor del navegador). */
export function openReceiptPdfInline(receiptRef) {
  const url = getReceiptPdfUrl(receiptRef);
  if (!url) return;
  const a = document.createElement("a");
  a.href = url;
  a.target = "_blank";
  a.rel = "noopener";
  document.body.appendChild(a);
  a.click();
  a.remove();
}

/** Fuerza la descarga del PDF (Content-Disposition: attachment). */
export function downloadReceiptPdf(receiptRef) {
  let url = getReceiptPdfUrl(receiptRef);
  if (!url) return;
  url += (url.includes("?") ? "&" : "?") + "download=true";
  const a = document.createElement("a");
  a.href = url;
  a.target = "_blank";
  a.rel = "noopener";
  document.body.appendChild(a);
  a.click();
  a.remove();
}

