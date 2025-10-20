// /js/cancelInboundOrderService.js
// Servicio enfocado en: listar pendientes, previsualizar una orden y cancelarla.
// Depende de: /js/config/env.js y /js/cancelInboundOrder.entity.js

import { apiUrl } from "../config/env.js";
import { PendingInboundOrderRef, InboundOrderPreview } from "../entities/cancelInboundOrder.entity.js";

// --- Utilidades HTTP ---------------------------------------------------------
const DEFAULT_TIMEOUT_MS = 15000;

async function httpFetch(path, { method = "GET", headers = {}, body, timeoutMs = DEFAULT_TIMEOUT_MS } = {}) {
  const controller = new AbortController();
  const id = setTimeout(() => controller.abort(), timeoutMs);

  try {
    const res = await fetch(`${apiUrl}${path}`, {
      method,
      headers: {
        Accept: "application/json",
        ...(body ? { "Content-Type": "application/json" } : {}),
        ...headers,
      },
      body: body ? JSON.stringify(body) : undefined,
      signal: controller.signal,
      credentials: "include", // soporta sesión/cookies si las usas ahora o en el futuro
    });

    const contentType = res.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");
    const payload = isJson ? await res.json().catch(() => ({})) : await res.text();

    if (!res.ok) {
      const message =
        (isJson && (payload.message || payload.error || payload.title)) ||
        (typeof payload === "string" && payload) ||
        `HTTP ${res.status}`;
      const err = new Error(message);
      err.status = res.status;
      err.payload = payload;
      throw err;
    }

    return payload;
  } catch (err) {
    if (err.name === "AbortError") {
      const e = new Error("La solicitud tardó demasiado. Inténtalo de nuevo.");
      e.status = 408;
      throw e;
    }
    throw err;
  } finally {
    clearTimeout(id);
  }
}

// --- API Público -------------------------------------------------------------
export const CancelInboundOrderService = {
  /**
   * Lista órdenes PENDING para selector.
   * GET /api/inbound-orders/pending
   * Devuelve: Array<PendingInboundOrderRef>
   */
  async listPending({ timeoutMs } = {}) {
    const data = await httpFetch("/inbound-orders/pending", { timeoutMs });
    if (!Array.isArray(data)) return [];
    // Mapear a Entity inmutable
    return data.map((dto) => PendingInboundOrderRef(dto));
  },

  /**
   * Previsualiza una orden (sin descargar PDF).
   * GET /api/inbound-orders/{id}/preview
   * Devuelve: InboundOrderPreview
   */
  async preview(orderId, { timeoutMs } = {}) {
    if (orderId === undefined || orderId === null) throw new Error("orderId es requerido");
    const dto = await httpFetch(`/inbound-orders/${encodeURIComponent(orderId)}/preview`, { timeoutMs });
    // Mapear a Entity inmutable
    return InboundOrderPreview(dto);
  },

  /**
   * Cancela una orden PENDING -> CANCELLED.
   * PATCH /api/inbound-orders/{id}/cancel
   * Devuelve objeto simple (no entity específica).
   * @param {number|string} orderId
   * @param {object} options
   * @param {string=} options.reason Motivo opcional
   * @param {string=} options.actor  (transitorio) si aún no usas Security, se envía como X-User
   * @param {number=} options.timeoutMs
   */
  async cancel(orderId, { reason, actor, timeoutMs } = {}) {
    if (orderId === undefined || orderId === null) throw new Error("orderId es requerido");

    const headers = {};
    if (actor && String(actor).trim()) {
      // Temporal hasta migrar a Spring Security/JWT
      headers["X-User"] = String(actor).trim();
    }

    const body = {};
    if (reason && String(reason).trim()) {
      body.reason = String(reason).trim();
    }

    const data = await httpFetch(`/inbound-orders/${encodeURIComponent(orderId)}/cancel`, {
      method: "PATCH",
      headers,
      body,
      timeoutMs,
    });

    // Respuesta simple; suficiente para actualizar la UI tras cancelación
    return {
      id: data?.id,
      orderNumber: data?.orderNumber ?? "",
      status: data?.status ?? "",
      cancelledAt: data?.cancelledAt ?? null,
      cancelReason: data?.cancelReason ?? null,
      cancelledBy: data?.cancelledBy ?? null,
    };
  },
};

// (Opcional) Exponer apiUrl para diagnósticos desde consola si hace falta
export const __CANCEL_IO_API_BASE = apiUrl;
