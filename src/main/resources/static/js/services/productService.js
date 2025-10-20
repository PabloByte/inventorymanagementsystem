// /js/services/productService.js
import { apiUrl } from "../config/env.js";
import Product from "../entities/Product.js";

const base = `${apiUrl}/products`;

// HTTP robusto: intenta JSON; si falla, usa texto. Muestra errores claros (400/404/409)
async function http(method, url, body) {
  const headers = { Accept: "application/json" };
  if (body !== undefined && body !== null) {
    headers["Content-Type"] = "application/json";
  }

  const res = await fetch(url, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  // Intentar leer JSON sin romper si no lo es
  let payload;
  let isJson = false;
  try {
    payload = await res.clone().json();
    isJson = true;
  } catch {
    try {
      payload = await res.text();
    } catch {
      payload = null;
    }
  }

  if (!res.ok) {
    let msg = `HTTP ${res.status}`;

    if (isJson && payload && typeof payload === "object") {
      // ProblemDetail de Spring Boot 3
      if (payload.detail) msg = payload.detail;
      else if (payload.title) msg = payload.title;

      // Validaciones (si tu Advice arma esto)
      if (Array.isArray(payload.errors) && payload.errors.length) {
        const joined = payload.errors
          .map(e => (e.field ? `${e.field}: ${e.message || e.defaultMessage || "inválido"}`
                             : (e.message || e.defaultMessage || "")))
          .filter(Boolean)
          .join(" • ");
        if (joined) msg = joined;
      }

      // Formatos comunes
      if (payload.message && !payload.detail) msg = payload.message;
      if (payload.error && !payload.detail && !payload.message) msg = payload.error;
    } else if (typeof payload === "string" && payload.trim()) {
      // Texto/HTML: toma <title> si existe o recorte
      const m = payload.match(/<title>(.*?)<\/title>/i);
      msg = m?.[1] || payload.slice(0, 280);
    }

    throw new Error(msg);
  }

  if (res.status === 204) return null;
  if (isJson) return payload;

  // Si vino texto pero era JSON válido con content-type incorrecto
  if (typeof payload === "string") {
    try { return JSON.parse(payload); } catch { return payload; }
  }
  return payload ?? null;
}

// ---------- API ----------

// Lista completa → Product[]
export async function listAll() {
  const data = await http("GET", `${base}/showAllProducts`);
  // Incluye nuevos campos: serial, lote, dimensiones, peso, estadoCertificado, observacion
  return Array.isArray(data) ? data.map(Product.fromDto) : [];
}

// Lista por reorden (ROP propio o threshold global) → Product[]
export async function listReorder(threshold = null) {
  const url = threshold == null
    ? `${base}/reorder`
    : `${base}/reorder?threshold=${encodeURIComponent(threshold)}`;
  const data = await http("GET", url);
  return Array.isArray(data) ? data.map(Product.fromDto) : [];
}

// Crear → acepta Product o plain object; devuelve Product
export async function create(payload) {
  // toInsertDto YA incluye serial, lote, dimensiones, peso, estadoCertificado, observacion
  const dto = payload instanceof Product ? payload.toInsertDto() : new Product(payload).toInsertDto();
  const data = await http("POST", `${base}`, dto);
  return Product.fromDto(data);
}

// Actualizar → acepta Product o plain object; devuelve Product
export async function update(id, payload) {
  const dto = payload instanceof Product ? payload.toInsertDto() : new Product(payload).toInsertDto();
  const data = await http("PUT", `${base}/${id}`, dto);
  return Product.fromDto(data);
}

// Eliminar → true en éxito
export async function remove(id) {
  await http("DELETE", `${base}/${id}`);
  return true;
}



