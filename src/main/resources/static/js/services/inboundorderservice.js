// Service con helper http robusto (mismo patrón de products)
import { apiUrl } from "../config/env.js";
import InboundOrder from "../entities/inboundorder.js";

const base = `${apiUrl}/inbound-orders`;

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

/* ====== Endpoints requeridos ====== */

// Productos por nombre → [{id, name}]
export async function fetchProductsByName() {
  return await http("GET", `${apiUrl}/products/showProductsByName`);
}

// Proveedores por nombre → [{id, name}]
export async function fetchSuppliersByName() {
  // Tu controller devuelve 202 ACCEPTED (res.ok === true), está bien.
  return await http("GET", `${apiUrl}/suppliers/showSuppliersByName`);
}

// Bodegas por nombre → [{id, name, code, address}]
export async function fetchWarehousesByName() {
  return await http("GET", `${apiUrl}/warehouses/showByName`);
}


export async function fetchProductById(id) {
  if (!id) throw new Error("id requerido");
  return await http("GET", `${apiUrl}/products/${encodeURIComponent(id)}`);
}


// Crear Inbound Order
export async function create(orderOrPlain) {
  const dto = orderOrPlain instanceof InboundOrder
    ? orderOrPlain.toInsertDto()
    : new InboundOrder(orderOrPlain).toInsertDto();

  const data = await http("POST", `${base}/create`, dto);
  return {
    order: data?.["Numero de Orden"] ?? null,   // contiene: id, orderNumber, supplier (nombre), etc.
    pdfDownloadUrl: data?.pdfDownloadUrl ?? null
  };
}



