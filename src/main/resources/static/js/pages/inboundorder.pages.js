// Page: maneja DOM y usa el service
import InboundOrder, { InboundOrderItem } from "../entities/inboundorder.js";
import {
  fetchProductsByName,
  fetchProductById,        // NUEVO
  fetchSuppliersByName,
  fetchWarehousesByName,
  create as createInboundOrder
} from "../services/inboundorderservice.js";

const $ = (sel, ctx=document) => ctx.querySelector(sel);
const isPositiveInt = (v) => Number.isInteger(Number(v)) && Number(v) > 0;
const escapeHtml = (s) => String(s)
  .replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;")
  .replaceAll('"',"&quot;").replaceAll("'","&#039;");

class InboundOrderPage {
  constructor() {
    if (!localStorage.getItem("isLoggedIn")) { window.location.href = "/pages/login.html"; return; }
    const cu = $("#currentUser"); if (cu) cu.textContent = localStorage.getItem("username") || "Admin";

    this.order = new InboundOrder({ status: "PENDING", warehouseId: 1 });

    // Cache de productos (id -> DTO completo)
    this.productCache = new Map();

    // refs
    this.$supplierSelect  = $("#io_supplierSelect");
    this.$status          = $("#io_status");
    this.$warehouseSelect = $("#io_warehouseSelect");
    this.$productSelect   = $("#io_productSelect");
    this.$productIdManual = $("#io_productIdManual");
    this.$fetchProductBtn = $("#io_fetchProductBtn"); // NUEVO
    this.$qty             = $("#io_quantity");
    this.$tbody           = $("#io_itemsTable tbody");
    this.$addItem         = $("#io_addItemBtn");
    this.$clear           = $("#io_clearBtn");
    this.$create          = $("#io_createOrderBtn");

    // preview refs
    this.$ppWrap  = $("#io_productPreview");
    this.$ppName  = $("#pp_name");
    this.$ppStat  = $("#pp_status");
    this.$ppSku   = $("#pp_sku");
    this.$ppBar   = $("#pp_barcode");
    this.$ppSer   = $("#pp_serial");
    this.$ppLot   = $("#pp_lote");
    this.$ppDim   = $("#pp_dimensiones");
    this.$ppPeso  = $("#pp_peso");
    this.$ppCert  = $("#pp_cert");
    this.$ppStock = $("#pp_stock");
    this.$ppDesc  = $("#pp_desc");
    this.$ppObs   = $("#pp_observ");

    this.bindEvents();
    this.initData();
  }

  bindEvents() {
    $("#signOut")?.addEventListener("click", (e) => {
      e.preventDefault(); localStorage.removeItem("isLoggedIn"); localStorage.removeItem("username");
      window.location.href = "/pages/login.html";
    });

    // Preview al cambiar selección
    this.$productSelect?.addEventListener("change", async () => {
      const id = Number(this.$productSelect.value || 0);
      if (isPositiveInt(id)) {
        await this.loadProductPreview(id);
        // si hay cantidad y quieres, podrías habilitar agregar directo
      } else {
        this.hidePreview();
      }
    });

    // Buscar por ID manual (click o Enter)
    this.$fetchProductBtn?.addEventListener("click", async (e) => {
      e.preventDefault();
      const id = Number(this.$productIdManual?.value || 0);
      if (!isPositiveInt(id)) { alert("Ingresa un productId válido."); return; }
      await this.loadProductPreview(id);
      // sincroniza combo si ese id existe en la lista
      if (this.$productSelect) {
        const opt = [...this.$productSelect.options].find(o => Number(o.value) === id);
        if (opt) this.$productSelect.value = String(id);
      }
    });
    this.$productIdManual?.addEventListener("keydown", async (e) => {
      if (e.key === "Enter") { e.preventDefault(); this.$fetchProductBtn?.click(); }
    });

    this.$addItem?.addEventListener("click", (e) => { e.preventDefault(); this.addItem(); });
    this.$clear?.addEventListener("click", (e) => { e.preventDefault(); this.clearItems(); });
    this.$create?.addEventListener("click", (e) => { e.preventDefault(); this.createOrder(); });

    this.$tbody?.addEventListener("click", (e) => {
      const btn = e.target.closest("[data-action='remove-item']");
      if (!btn) return;
      this.order.removeItemAt(Number(btn.dataset.idx));
      this.renderItems();
    });

    [$("#io_closeModalBtn"), $("#io_closeModalBtn2")].forEach(b => b?.addEventListener("click", () => this.hideModal()));
    const modal = $("#io_resultModal");
    if (modal) window.addEventListener("click", (evt) => { if (evt.target === modal) this.hideModal(); });
  }

  async initData() {
    // Proveedores
    if (this.$supplierSelect) {
      this.$supplierSelect.innerHTML = `<option value="">Cargando proveedores...</option>`;
      try {
        const suppliers = await fetchSuppliersByName();
        this.$supplierSelect.innerHTML =
          `<option value="">-- Selecciona un Proveedor --</option>` +
          suppliers.map(s => `<option value="${s.id}">${escapeHtml(s.name ?? "—")}</option>`).join("");
      } catch (e) {
        console.error(e);
        this.$supplierSelect.innerHTML = `<option value="">-- No hay proveedores (error) --</option>`;
      }
    }

    // Productos (lista básica)
    if (this.$productSelect) {
      this.$productSelect.innerHTML = `<option value="">Cargando productos...</option>`;
      try {
        const products = await fetchProductsByName();
        this.$productSelect.innerHTML =
          `<option value="">-- Selecciona los productos --</option>` +
          products.map(p => `<option value="${p.id}">${escapeHtml(p.name ?? "—")}</option>`).join("");
      } catch (e) {
        console.error(e);
        this.$productSelect.innerHTML = `<option value="">Lista no disponible — use productId manual</option>`;
      }
    }

    // Bodegas
    if (this.$warehouseSelect) {
      this.$warehouseSelect.innerHTML = `<option value="">Cargando bodegas...</option>`;
      try {
        const warehouses = await fetchWarehousesByName();
        this.$warehouseSelect.innerHTML =
          `<option value="" disabled selected>-- Selecciona una bodega --</option>` +
          warehouses.map(w => {
            const label = w.code ? `${w.name} (${w.code})` : w.name;
            return `<option value="${w.id}">${label}</option>`;
          }).join("");
      } catch (e) {
        console.error(e);
        this.$warehouseSelect.innerHTML = `<option value="">Error cargando bodegas</option>`;
      }
    }

    // Estado / Bodega predeterminados
    if (this.$status) this.$status.value = "PENDING";
    if (this.$warehouseSelect) this.$warehouseSelect.value = "1";

    this.renderItems();
  }

  async loadProductPreview(id) {
    try {
      let p = this.productCache.get(id);
      if (!p) {
        p = await fetchProductById(id);
        this.productCache.set(id, p);
      }
      this.renderPreview(p);
    } catch (e) {
      console.error(e);
      alert(e.message || "No fue posible cargar el producto.");
      this.hidePreview();
    }
  }

  renderPreview(p) {
    if (!this.$ppWrap) return;
    this.$ppWrap.style.display = "block";
    this.$ppName.textContent  = p?.name ?? "—";
    this.$ppStat.textContent  = p?.status ?? "—";
    this.$ppSku.textContent   = p?.sku ?? "—";
    this.$ppBar.textContent   = p?.barcode ?? "—";
    this.$ppSer.textContent   = p?.serial ?? "—";
    this.$ppLot.textContent   = p?.lote ?? "—";
    this.$ppDim.textContent   = p?.dimensiones ?? "—";
    this.$ppPeso.textContent  = (p?.peso ?? null) !== null ? `${p.peso}` : "—";
    this.$ppCert.textContent  = p?.estadoCertificado ?? "—";
    this.$ppStock.textContent = (p?.stock ?? null) !== null ? `${p.stock}` : "—";
    this.$ppDesc.textContent  = p?.description ?? "—";
    this.$ppObs.textContent   = p?.observacion ?? "—";
    // badge simple
    this.$ppStat.className = "badge " + (String(p?.status).toUpperCase() === "ACTIVE" ? "badge-success" : "badge-warning");
  }

  hidePreview() { if (this.$ppWrap) this.$ppWrap.style.display = "none"; }

  addItem() {
    const selectVal = this.$productSelect?.value?.trim();
    const manualVal = this.$productIdManual?.value?.trim();
    const qtyVal    = this.$qty?.value;

    let productId = null; let name = "";
    if (selectVal) {
      productId = Number(selectVal);
      name = this.$productSelect.options[this.$productSelect.selectedIndex]?.text || "";
    } else if (manualVal) {
      productId = Number(manualVal);
      name = `id:${productId}`;
    } else {
      alert("Selecciona un producto o ingresa productId manual.");
      return;
    }

    if (!isPositiveInt(productId) || !isPositiveInt(qtyVal)) {
      alert("Cantidad o productId inválido."); return;
    }

    // Si tenemos detalle, usa el nombre real + cache
    const detail = this.productCache.get(productId);
    const displayName = detail?.name || name;

    this.order.addItem(new InboundOrderItem({
      productId,
      quantity: Number(qtyVal),
      productName: displayName
    }));

    if (this.$productSelect) this.$productSelect.value = "";
    if (this.$productIdManual) this.$productIdManual.value = "";
    if (this.$qty) this.$qty.value = "1";

    this.renderItems();
  }

  renderItems() {
    if (!this.$tbody) return;
    if (!this.order.items.length) {
      this.$tbody.innerHTML = `<tr><td colspan="9">No hay items</td></tr>`;
      return;
    }

    // Render con datos de cache si existen (SKU/serial/lote/dimensiones/peso)
    this.$tbody.innerHTML = this.order.items.map((it, idx) => {
      const p = this.productCache.get(it.productId);
      const sku   = p?.sku ?? "—";
      const serial= p?.serial ?? "—";
      const lote  = p?.lote ?? "—";
      const dim   = p?.dimensiones ?? "—";
      const peso  = (p?.peso ?? null) !== null ? `${p.peso}` : "—";
      const name  = it.productName || p?.name || "";

      return `
        <tr>
          <td>${it.productId}</td>
          <td class="text-wrap">${escapeHtml(name)}</td>
          <td>${escapeHtml(sku)}</td>
          <td>${escapeHtml(serial)}</td>
          <td>${escapeHtml(lote)}</td>
          <td>${escapeHtml(dim)}</td>
          <td>${escapeHtml(peso)}</td>
          <td>${it.quantity}</td>
          <td><button class="btn btn-sm btn-danger" data-action="remove-item" data-idx="${idx}">Eliminar</button></td>
        </tr>
      `;
    }).join("");

    // Para ítems recién agregados sin detalle, dispara fetch en background y vuelve a renderizar
    const idsToFetch = this.order.items
      .map(it => it.productId)
      .filter(id => !this.productCache.has(id));

    if (idsToFetch.length) {
      Promise.allSettled(idsToFetch.map(id => fetchProductById(id)))
        .then(results => {
          results.forEach((r, i) => { if (r.status === "fulfilled") this.productCache.set(idsToFetch[i], r.value); });
          this.$tbody && this.renderItems();
        })
        .catch(() => {/* ignore */});
    }
  }

  clearItems() { this.order.clearItems(); this.renderItems(); }

  async createOrder() {
    try {
      const supplierId = this.$supplierSelect?.value;
      if (!supplierId) { alert("Debes seleccionar un proveedor."); return; }
      if (!this.order.items.length) { alert("Agrega al menos un item."); return; }

      this.order.supplierId = Number(supplierId);
      this.order.status = (this.$status?.value || "PENDING").toUpperCase();
      this.order.warehouseId = Number(this.$warehouseSelect?.value || 1);

      const { order, pdfDownloadUrl } = await createInboundOrder(this.order);

      const numberToShow = order?.orderNumber || order?.id || "—";
      $("#io_orderNumber").textContent = numberToShow;

      const $pdf = $("#io_pdfLink");
      if ($pdf) {
        if (pdfDownloadUrl) { $pdf.href = pdfDownloadUrl; $pdf.style.display = "inline-block"; }
        else { $pdf.removeAttribute("href"); $pdf.style.display = "none"; }
      }

      this.showModal();
      this.clearItems();
      this.hidePreview();
    } catch (err) {
      console.error(err);
      alert(err.message || "Hubo un error al crear la orden.");
    }
  }

  showModal() { $("#io_resultModal")?.style && ( $("#io_resultModal").style.display = "flex" ); }
  hideModal() { $("#io_resultModal")?.style && ( $("#io_resultModal").style.display = "none" ); }
}

document.addEventListener("DOMContentLoaded", () => { new InboundOrderPage(); });




