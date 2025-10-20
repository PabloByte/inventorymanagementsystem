// /js/cancelInboundOrder.pages.js
// Orquestación de la vista: selector de pendientes -> preview -> cancelar (modal)

import { CancelInboundOrderService } from "../services/cancelInboundOrderService.js";




// ---------------- Estado ----------------
const state = {
  pending: [],
  selectedId: null,
  preview: null,
  loading: {
    pending: false,
    preview: false,
    cancel: false,
  },
};

// ---------------- Utilidades UI ----------------
const $ = (sel) => document.querySelector(sel);

function showLoader(visible) {
  const box = $("#pageLoader");
  if (!box) return;
  box.classList.toggle("hidden", !visible);
}

function toast(msg, kind = "info", ms = 2800) {
  const el = $("#toast");
  if (!el) {
    console[kind === "error" ? "error" : "log"]("[toast]", msg);
    return;
  }
  el.textContent = msg;
  el.dataset.kind = kind; // para estilos .toast[data-kind="error"] { ... }
  el.classList.remove("hidden");
  clearTimeout(el.__t);
  el.__t = setTimeout(() => el.classList.add("hidden"), ms);
}

function formatMoney(v) {
  if (v == null || Number.isNaN(Number(v))) return "—";
  try {
    return new Intl.NumberFormat("es-CO", { style: "currency", currency: "COP", maximumFractionDigits: 0 }).format(v);
  } catch {
    return `${v}`;
  }
}
function formatDateTime(iso) {
  if (!iso) return "—";
  try {
    const d = new Date(iso);
    const dtf = new Intl.DateTimeFormat("es-CO", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
    return dtf.format(d);
  } catch {
    return iso;
  }
}

// Accesibilidad básica para modal
function bindModalAccessibility(modal) {
  if (!modal) return;
  function onKey(e) {
    if (e.key === "Escape") closeModal();
  }
  function onBackdrop(e) {
    if (e.target === modal) closeModal();
  }
  modal.__onKey = onKey;
  modal.__onBackdrop = onBackdrop;
  document.addEventListener("keydown", onKey);
  modal.addEventListener("click", onBackdrop);
}
function unbindModalAccessibility(modal) {
  if (!modal) return;
  if (modal.__onKey) document.removeEventListener("keydown", modal.__onKey);
  if (modal.__onBackdrop) modal.removeEventListener("click", modal.__onBackdrop);
  delete modal.__onKey;
  delete modal.__onBackdrop;
}

function openModal(summaryText = "") {
  const modal = $("#cancelModal");
  if (!modal) return;
  $("#cancelSummary") && ($("#cancelSummary").textContent = summaryText);
  modal.classList.remove("hidden");
  bindModalAccessibility(modal);
  $("#cancelReason")?.focus();
}
function closeModal() {
  const modal = $("#cancelModal");
  if (!modal) return;
  unbindModalAccessibility(modal);
  modal.classList.add("hidden");
  // Limpieza del form
  $("#cancelReason") && ($("#cancelReason").value = "");
}

// ---------------- Render ----------------
function renderPendingSelect() {
  const sel = $("#pendingSelect");
  if (!sel) return;

  sel.innerHTML = "";
  const placeholder = document.createElement("option");
  placeholder.value = "";
  placeholder.textContent = state.pending.length ? "Seleccione una orden pendiente..." : "No hay órdenes pendientes";
  placeholder.disabled = true;
  placeholder.selected = true;
  sel.appendChild(placeholder);

  state.pending.forEach((o) => {
    const opt = document.createElement("option");
    opt.value = o.id;
    opt.textContent = `${o.orderNumber} · ${o.supplierName} · ${o.warehouse.code}`;
    sel.appendChild(opt);
  });
}

function renderPreview() {
  const box = $("#previewPanel");
  const btnCancel = $("#btnCancel");
  if (!box) return;

  if (!state.preview) {
    box.innerHTML = `
      <div class="empty">
        <p>Seleccione una orden para ver la previsualización.</p>
      </div>`;
    btnCancel && (btnCancel.disabled = true);
    return;
  }

  const o = state.preview; // Entity InboundOrderPreview
  const statusBadge =
    o.isCancelled
      ? `<span class="badge badge-cancelled">Cancelada</span>`
      : o.isCancellable
      ? `<span class="badge badge-pending">Pendiente</span>`
      : `<span class="badge">${o.status}</span>`;

  const itemsRows = o.items
    .map(
      (it) => `
      <tr>
        <td>${it.sku}</td>
        <td>${it.name}</td>
        <td>${it.uom ?? "—"}</td>
        <td class="num">${it.orderedQty}</td>
        <td class="num">${it.unitPrice != null ? formatMoney(it.unitPrice) : "—"}</td>
        <td class="num">${it.subtotal != null ? formatMoney(it.subtotal) : "—"}</td>
      </tr>`
    )
    .join("");

  box.innerHTML = `
    <article class="card">
      <header class="card-header">
        <div class="left">
          <h3>Numero de Orden : ${o.orderNumber}</h3>
          <div class="meta">
            <span>Fecha de Creacion: ${formatDateTime(o.createdAt)}</span>
            <span>Proveedor: ${o.supplier.name}</span>
            <span>Bodega: ${o.warehouse.name} (${o.warehouse.code})</span>
          </div>
        </div>
        <div class="right">
          ${statusBadge}
        </div>
      </header>
      <section class="card-body">
        <table class="table items">
          <thead>
            <tr>
              <th>Codigo</th><th>Producto</th><th>UoM</th><th class="num">Cant.</th><th class="num">Precio</th><th class="num">Subtotal</th>
            </tr>
          </thead>
          <tbody>
            ${itemsRows || `<tr><td colspan="6" class="empty">Sin ítems</td></tr>`}
          </tbody>
          <tfoot>
            <tr>
              <td colspan="3">Total Productos: <strong>${o.totals.lines}</strong></td>
              <td class="num"><strong>${o.totals.units}</strong></td>
              <td class="num">Subtotal</td>
              <td class="num"><strong>${formatMoney(o.totals.subtotal)}</strong></td>
            </tr>
        
            <tr>
              <td colspan="5" class="num total">Total</td>
              <td class="num total"><strong>${formatMoney(o.totals.grandTotal)}</strong></td>
            </tr>
          </tfoot>
        </table>

        ${
          o.isCancelled
            ? `
          <div class="alert alert-warning mt">
            <p><strong>Orden cancelada</strong></p>
            <p>Motivo: ${o.cancelReason ?? "—"}</p>
            <p>Cancelada por: ${o.cancelledBy ?? "—"} el ${formatDateTime(o.cancelledAt)}</p>
          </div>`
            : ""
        }
      </section>
    </article>
  `;

  // Habilitar/Deshabilitar botón cancelar
  if (btnCancel) {
    btnCancel.disabled = !o.isCancellable;
  }
}

// ---------------- Acciones ----------------
async function loadPending() {
  try {
    state.loading.pending = true;
    showLoader(true);
    state.pending = await CancelInboundOrderService.listPending();
    renderPendingSelect();
  } catch (err) {
    toast(err.message || "No se pudo cargar la lista de pendientes", "error");
  } finally {
    state.loading.pending = false;
    showLoader(false);
  }
}

async function loadPreview(orderId) {
  if (!orderId) {
    state.preview = null;
    renderPreview();
    return;
  }
  try {
    state.loading.preview = true;
    showLoader(true);
    state.preview = await CancelInboundOrderService.preview(orderId);
    renderPreview();
  } catch (err) {
    state.preview = null;
    renderPreview();
    toast(err.message || "No se pudo cargar la previsualización", "error");
  } finally {
    state.loading.preview = false;
    showLoader(false);
  }
}

function onSelectChange() {
  const sel = $("#pendingSelect");
  state.selectedId = sel?.value ? Number(sel.value) : null;
  // Carga automática de preview al elegir
  if (state.selectedId) {
    loadPreview(state.selectedId);
  } else {
    state.preview = null;
    renderPreview();
  }
}

function onClickLoadPreview() {
  if (!state.selectedId) {
    toast("Selecciona una orden primero", "error");
    return;
  }
  loadPreview(state.selectedId);
}

function onClickOpenCancel() {
  if (!state.preview || !state.preview.isCancellable) {
    toast("Esta orden no se puede cancelar", "error");
    return;
  }
  const summary = `Vas a cancelar la orden ${state.preview.orderNumber}. Esta acción es permanente.`;
  openModal(summary);
}

async function onClickConfirmCancel() {
  if (!state.preview || !state.preview.isCancellable) {
    toast("Esta orden no se puede cancelar", "error");
    return;
  }
  const reason = $("#cancelReason")?.value ?? "";
  const actor = $("#cancelActor")?.value ?? ""; // temporal hasta tener Security

  try {
    state.loading.cancel = true;
    showLoader(true);
    const res = await CancelInboundOrderService.cancel(state.preview.id, { reason, actor });

    closeModal();
    toast(`Orden ${res.orderNumber} cancelada`, "info");

    // Refrescar preview (ya vendrá como CANCELLED) y lista de pendientes
    await Promise.all([loadPending(), loadPreview(state.preview.id)]);
  } catch (err) {
    toast(err.message || "No se pudo cancelar la orden", "error");
  } finally {
    state.loading.cancel = false;
    showLoader(false);
  }
}

// ---------------- Wire-up ----------------
function bindEvents() {
  $("#pendingSelect")?.addEventListener("change", onSelectChange);
  $("#btnLoadPreview")?.addEventListener("click", onClickLoadPreview);
  $("#btnCancel")?.addEventListener("click", onClickOpenCancel);
  $("#btnConfirmCancel")?.addEventListener("click", onClickConfirmCancel);
  $("#btnCloseModal")?.addEventListener("click", closeModal);
}

async function init() {
  bindEvents();
  renderPreview(); // estado vacío
  await loadPending();
}

document.addEventListener("DOMContentLoaded", init);
