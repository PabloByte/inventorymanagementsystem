import InboundReceiptDraft, { InboundReceiptLine } from "../entities/inboundreceipt.js";
import { listPendingOrders, getOrderDetail, receiveAndAdjust } from "../services/inboundreceiptservice.js";
import { getReceiptPdfUrl, openReceiptPdfInline, downloadReceiptPdf } from "../services/inboundreceiptservice.js";


const $ = (sel, ctx=document) => ctx.querySelector(sel);
const fmtCOP = v => new Intl.NumberFormat("es-CO", { style: "currency", currency: "COP", maximumFractionDigits: 0 }).format(Number(v||0));
const escapeHtml = (s) => String(s).replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll('"',"&quot;").replaceAll("'","&#039;");

class InboundReceiptPage {
  constructor() {
    if (!localStorage.getItem("isLoggedIn")) { window.location.href = "/pages/login.html"; return; }
    const cu = $("#currentUser"); if (cu) cu.textContent = localStorage.getItem("username") || "Admin";

    this.draft = new InboundReceiptDraft();

    // refs
    this.$orderSelect   = $("#ir_orderSelect");
    this.$loadBtn       = $("#ir_loadOrderBtn");

    this.$supplier      = $("#ir_supplier");
    this.$warehouse     = $("#ir_warehouse");
    this.$status        = $("#ir_status");

    this.$receivedBy    = $("#ir_receivedBy");
    this.$note          = $("#ir_note");
    this.$tbody         = $("#ir_itemsTable tbody");

    this.$receiveBtn    = $("#ir_receiveBtn");
    this.$clearBtn      = $("#ir_clearBtn");

    // Totales (panel nuevo)
    this.$t_lines   = $("#ir_t_lines");
    this.$t_qty     = $("#ir_t_qty");
    this.$t_cost    = $("#ir_t_cost");
    this.$o_ordered = $("#ir_o_ordered");
    this.$o_recv    = $("#ir_o_recv");
    this.$o_pend    = $("#ir_o_pend");

    // modal
    this.$modal     = $("#ir_resultModal");
    this.$recNum    = $("#ir_receiptNumber");
    this.$pdfLink   = $("#ir_pdfLink");
    this.$close1    = $("#ir_closeModalBtn");
    this.$close2    = $("#ir_closeModalBtn2");

    this.bind();
    this.init();
  }

  bind() {
    $("#signOut")?.addEventListener("click", (e)=> {
      e.preventDefault(); localStorage.removeItem("isLoggedIn"); localStorage.removeItem("username");
      window.location.href = "/pages/login.html";
    });

    this.$loadBtn?.addEventListener("click", (e) => { e.preventDefault(); this.loadSelectedOrder(); });
    this.$receiveBtn?.addEventListener("click", (e)=> { e.preventDefault(); this.submitReceipt(); });
    this.$clearBtn?.addEventListener("click", (e)=> { e.preventDefault(); this.clear(); });

    // inputs que impactan el draft (recibido por / nota)
    this.$receivedBy?.addEventListener("input", ()=> { this.draft.receivedBy = this.$receivedBy.value; });
    this.$note?.addEventListener("input", ()=> { this.draft.note = this.$note.value; });

    // cerrar modal
    [this.$close1, this.$close2].forEach(b=> b?.addEventListener("click", ()=> this.hideModal()));
    if (this.$modal) window.addEventListener("click", (evt)=> { if (evt.target === this.$modal) this.hideModal(); });
  }

  async init() {
    // Cargar órdenes PENDING
    if (this.$orderSelect) {
      this.$orderSelect.innerHTML = `<option value="">Cargando órdenes PENDING...</option>`;
      try {
        const data = await listPendingOrders();
        this.$orderSelect.innerHTML =
          `<option value="" selected disabled>-- Selecciona una orden --</option>` +
          (Array.isArray(data) ? data.map(o => {
            // o.orderNumber, o.supplier, o.warehouse
            const label = [o.orderNumber, o.supplier, o.warehouse].filter(Boolean).join(" — ");
            return `<option value="${escapeHtml(o.orderNumber)}">${escapeHtml(label)}</option>`;
          }).join("") : `<option value="">(sin datos)</option>`);
      } catch (e) {
        console.error(e);
        this.$orderSelect.innerHTML = `<option value="">Error cargando órdenes</option>`;
      }
    }
    this.renderLines();
    this.renderTotals(); // 0s
  }

  async loadSelectedOrder() {
    const orderNumber = this.$orderSelect?.value;
    if (!orderNumber) { alert("Selecciona una orden."); return; }
    try {
      const detail = await getOrderDetail(orderNumber);
      // { orderNumber, status, supplier, warehouse, items: [{productId,sku,name,orderedQty,receivedSoFar,unitCost}] }

      this.draft.orderNumber = detail.orderNumber;
      this.draft.clear();
      (detail.items || []).forEach(it => {
        this.draft.addLine(new InboundReceiptLine(it));
      });

      // pinta cabecera
      if (this.$supplier)  this.$supplier.value  = detail.supplier || "—";
      if (this.$warehouse) this.$warehouse.value = detail.warehouse || "—";
      if (this.$status)    this.$status.value    = detail.status || "—";

      // limpia campos cabecera del recibo
      if (this.$receivedBy) this.$receivedBy.value = "";
      if (this.$note) this.$note.value = "";

      this.renderLines();
      this.renderTotals();
    } catch (e) {
      console.error(e);
      alert(e.message || "No se pudo cargar la orden.");
    }
  }

  
  renderLines() {
  if (!this.$tbody) return;

  const rows = this.draft.items.map((l, idx) => {
    const pendingBefore = Math.max(0, l.orderedQty - l.receivedSoFar);
    return `
      <tr data-idx="${idx}">
        <td>
          <div class="name"><strong>${escapeHtml(l.sku || "")}</strong> • ${escapeHtml(l.name)}</div>
        </td>
        <td class="ta-center">${l.orderedQty}</td>
        <td class="ta-center">${pendingBefore}</td>
        <td class="ta-center">
          <input type="number" min="0" step="1" class="form-control ir-input ir-qty" value="${l.receiveQty || 0}" />
        </td>
        <td class="ta-center">
          <span class="ir-uc" data-uc="${Number(l.unitCost || 0)}">${fmtCOP(l.unitCost || 0)}</span>
        </td>
        <td>
          <input type="text" class="form-control ir-input ir-note" value="${escapeHtml(l.note)}" placeholder="Nota (opcional)"/>
        </td>
        <td class="ta-center">
          <button class="btn btn-sm btn-danger" data-action="clear-line">Limpiar</button>
        </td>
      </tr>
    `;
  });

  this.$tbody.innerHTML = rows.length ? rows.join("") : `<tr><td colspan="7">Cargue una orden para ver los ítems.</td></tr>`;

  // Bind solo a qty y note (unit cost ya NO es editable)
  this.$tbody.querySelectorAll("tr[data-idx]").forEach(tr => {
    const idx = Number(tr.getAttribute("data-idx"));
    const qty = tr.querySelector(".ir-qty");
    const note = tr.querySelector(".ir-note");
    const btnClear = tr.querySelector("[data-action='clear-line']");

    qty?.addEventListener("input", () => {
      this.draft.items[idx].receiveQty = Math.max(0, Number(qty.value || 0));
      this.renderTotals(); // live totals
    });

    note?.addEventListener("input", () => {
      this.draft.items[idx].note = note.value;
    });

    btnClear?.addEventListener("click", (e) => {
      e.preventDefault();
      this.draft.items[idx].receiveQty = 0;
      this.draft.items[idx].note = "";
      this.renderLines();
      this.renderTotals();
    });
  });
}

computeOrderTotalsFromDetail() {
  const ordered = this.draft.items.reduce((s,l)=> s + Number(l.orderedQty || 0), 0);
  const recv    = this.draft.items.reduce((s,l)=> s + Number(l.receivedSoFar || 0), 0);
  const pend    = Math.max(0, ordered - recv);
  return { orderTotalOrderedQty: ordered, orderTotalReceivedQty: recv, orderTotalPendingQty: pend };
}



renderTotals(serverTotals = null) {
  // Totales “live” del borrador (arriba)
  if (this.$t_lines) this.$t_lines.value = String(this.draft.totalLines);
  if (this.$t_qty)   this.$t_qty.value   = String(this.draft.totalQty);
  if (this.$t_cost)  this.$t_cost.value  = fmtCOP(this.draft.totalCost);

  // Totales de la orden (abajo)
  const base = this.computeOrderTotalsFromDetail(); // antes del POST (detalle cargado)
  if (serverTotals) {
    // después del POST, usa los del backend
    if (this.$o_ordered) this.$o_ordered.value = String(serverTotals.orderTotalOrderedQty ?? base.orderTotalOrderedQty);
    if (this.$o_recv)    this.$o_recv.value    = String(serverTotals.orderTotalReceivedQty ?? base.orderTotalReceivedQty);
    if (this.$o_pend)    this.$o_pend.value    = String(serverTotals.orderTotalPendingQty ?? base.orderTotalPendingQty);
  } else {
    // al cargar detalle (antes del POST)
    if (this.$o_ordered) this.$o_ordered.value = String(base.orderTotalOrderedQty);
    if (this.$o_recv)    this.$o_recv.value    = String(base.orderTotalReceivedQty);
    if (this.$o_pend)    this.$o_pend.value    = String(base.orderTotalPendingQty);
  }
}


async submitReceipt() {
  try {
    if (!this.draft.orderNumber) { alert("Primero selecciona y carga una orden."); return; }
    const body = this.draft.toReceiveBody();
    if (!body.items.length) { alert("No hay cantidades para recibir."); return; }

    // POST → InboundReceiptResponseDto
    const result = await receiveAndAdjust(this.draft.orderNumber, body);

    // Actualiza estado de la orden si vino
    if (this.$status && result?.order?.status) {
      this.$status.value = result.order.status;
    }

    // Totales “orden” después del POST (usa los enviados por backend)
    if (result?.totals) {
      this.renderTotals(result.totals);
    } else {
      // Fallback: recalcula con lo que tengas en memoria
      this.renderTotals();
    }

    // Resetea cantidades ingresadas en la UI (recibir ahora) y re-render
    this.draft.items.forEach(l => { l.receiveQty = 0; l.note = ""; });
    this.renderLines(); // vuelve a pintar la tabla sin modificar unitCost (solo lectura)

    // Modal: número de recibo + link PDF (si tu backend lo agrega)

    // Modal: número de recibo + link PDF
const rnum = result?.receipt?.number || result?.receiptNumber || "—";
if (this.$recNum) this.$recNum.textContent = rnum;

// 1) intenta usar lo que venga del backend (pdfDownloadUrl / pdfUrl)
// 2) si no viene, construye con receiptId
const pdfUrl =
  result?.pdfDownloadUrl ||
  result?.pdfUrl ||
  getReceiptPdfUrl(
    result?.receiptId ??
    result?.receipt?.id ??
    result?.receipt?.receiptId ??
    null
  );

if (this.$pdfLink) {
  if (pdfUrl) {
    this.$pdfLink.href = pdfUrl;
    this.$pdfLink.target = "_blank";
    this.$pdfLink.rel = "noopener";
    this.$pdfLink.textContent = "Ver recibo (PDF)";
    this.$pdfLink.style.display = "inline-block";
  } else {
    this.$pdfLink.removeAttribute("href");
    this.$pdfLink.style.display = "none";
  }
}

// (Opcional) Si en tu HTML existen botones extra para ver/descargar:
const $viewBtn = document.querySelector("#ir_viewPdfBtn");
const $downBtn = document.querySelector("#ir_downloadPdfBtn");
if (pdfUrl && $viewBtn) {
  $viewBtn.onclick = (e) => { e.preventDefault(); openReceiptPdfInline(pdfUrl); };
}
if (pdfUrl && $downBtn) {
  $downBtn.onclick = (e) => { e.preventDefault(); downloadReceiptPdf(pdfUrl); };
}

   

    this.showModal();
  } catch (e) {
    console.error(e);
    alert(e.message || "Error al crear el recibo.");
  }
}






  

  clear() {
    this.draft = new InboundReceiptDraft();
    if (this.$supplier)  this.$supplier.value  = "";
    if (this.$warehouse) this.$warehouse.value = "";
    if (this.$status)    this.$status.value    = "";
    if (this.$receivedBy) this.$receivedBy.value = "";
    if (this.$note) this.$note.value = "";
    this.renderLines();
    this.renderTotals();
  }

  showModal() { if (this.$modal) this.$modal.style.display = "flex"; }
  hideModal() { if (this.$modal) this.$modal.style.display = "none"; }
}

document.addEventListener("DOMContentLoaded", ()=> new InboundReceiptPage());
