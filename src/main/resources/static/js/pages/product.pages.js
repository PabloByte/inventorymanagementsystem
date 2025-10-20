// /js/pages/product.page.js
import { apiUrl } from "../config/env.js";
import { listAll, listReorder, create, update, remove } from "../services/productService.js";
import Product from "../entities/Product.js";

// ======================
// Helpers y estado
// ======================
const $  = (sel, ctx=document) => ctx.querySelector(sel);
const $$ = (sel, ctx=document) => [...ctx.querySelectorAll(sel)];
const formatCOP = v => new Intl.NumberFormat("es-CO", { style: "currency", currency: "COP", maximumFractionDigits: 0 }).format(Number(v||0));

const form    = $("#product-form");
const msg     = $("#product-message");
const tbody   = $("#products-tbody");
const selCat  = $("#categoryId");
const selSup  = $("#supplierId");
const btnSave = $("#btn-save");

const modeSel = $("#reorder-mode");
const thrInp  = $("#reorder-threshold");
const btnReor = $("#btn-reorder");
const btnRef  = $("#btn-refresh");
const btnClr  = $("#btn-reset-form");
const reCount = $("#reorder-count");

// Buscador
const searchInp   = $("#search-input");
const btnSearch   = $("#btn-search");
const btnClearSrh = $("#btn-clear-search");

// Estado en memoria para búsqueda en vivo
let productsCache = [];   // productos completos
let currentTerm   = "";   // término de búsqueda activo
let sortKey = null;        // p.ej. "name", "price"
let sortDir = "asc";       // "asc" | "desc"

// Debounce simple
function debounce(fn, ms = 300) {
  let t;
  return (...args) => {
    clearTimeout(t);
    t = setTimeout(() => fn(...args), ms);
  };
}

// ordenamiento
function normalizeStr(v) {
  return String(v ?? "").toLowerCase();
}
function cmp(a, b) {
  if (a < b) return -1;
  if (a > b) return  1;
  return 0;
}
function applySort(list) {
  if (!sortKey) return list.slice(); // sin ordenar, pero copia defensiva
  const numericKeys = ["price", "stock", "reorderPoint", "peso"];
  const isNumeric = numericKeys.includes(sortKey);
  const factor = sortDir === "asc" ? 1 : -1;

  return list.slice().sort((p1, p2) => {
    const v1 = p1[sortKey];
    const v2 = p2[sortKey];
    if (isNumeric) {
      return factor * cmp(Number(v1 ?? 0), Number(v2 ?? 0));
    } else {
      return factor * cmp(normalizeStr(v1), normalizeStr(v2));
    }
  });
}

function setHeaderSortUI() {
  document.querySelectorAll("th.th-sort").forEach(th => {
    th.classList.remove("sorted-asc", "sorted-desc");
    const key = th.dataset.sort;
    if (key && key === sortKey) {
      th.classList.add(sortDir === "asc" ? "sorted-asc" : "sorted-desc");
      th.dataset.label = th.dataset.label || th.textContent.trim();
      th.innerHTML = `${th.dataset.label} ${sortDir === "asc" ? "↑" : "↓"}`;
    } else {
      if (th.dataset.label) th.textContent = th.dataset.label;
    }
  });
}

function attachSortHandlers() {
  document.querySelectorAll("th.th-sort").forEach(th => {
    th.addEventListener("click", () => {
      const key = th.dataset.sort;
      if (!key) return;
      if (sortKey === key) {
        sortDir = sortDir === "asc" ? "desc" : "asc";
      } else {
        sortKey = key;
        sortDir = "asc";
      }
      setHeaderSortUI();
      renderAll();
    });
  });
}

// ======================
// Toasts
// ======================
function toast(text, ok = true, ms = 3000) {
  if (!msg) { alert(text); return; }
  msg.textContent = text;
  msg.className = ok ? "alert alert-success" : "alert alert-danger";
  setTimeout(() => { msg.textContent = ""; msg.className = ""; }, ms);
}

// ======================
// Envío seguro (evita dobles)
// ======================
let isSubmitting = false;
function beginSubmit() {
  isSubmitting = true;
  if (btnSave) {
    btnSave.disabled = true;
    btnSave.dataset.originalText = btnSave.textContent;
    btnSave.textContent = "Guardando...";
  }
}
function endSubmit() {
  isSubmitting = false;
  if (btnSave) {
    btnSave.disabled = false;
    btnSave.textContent = btnSave.dataset.originalText || "Guardar";
  }
}

// ======================
// Resaltado de búsqueda
// ======================
function escapeRegExp(s) { return s.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"); }
function highlight(text, term) {
  const str = String(text ?? "");
  const t = (term || "").trim();
  if (!t) return str;
  const re = new RegExp(escapeRegExp(t), "ig");
  return str.replace(re, m => `<mark>${m}</mark>`);
}

// ======================
// Form / Fila
// ======================
function clearForm() {
  if (!form) return;
  form.reset();
  const idInput = form.querySelector('[name="id"]');
  if (idInput) idInput.value = "";

  // limpiar selects con placeholder
  if (form.categoryId) form.categoryId.value = "";
  if (form.supplierId) form.supplierId.value = "";
}

function fillForm(p /** @type {Product} */) {
  if (!form) return;
  if (form.id) form.id.value = p.id ?? "";
  if (form.sku) form.sku.value = p.sku ?? "";
  if (form.barcode) form.barcode.value = p.barcode ?? "";
  if (form.name) form.name.value = p.name ?? "";
  if (form.description) form.description.value = p.description ?? "";
  if (form.price) form.price.value = p.price ?? 0;
  if (form.reorderPoint) form.reorderPoint.value = p.reorderPoint ?? 5;
  if (form.status) form.status.value = p.status ?? "ACTIVE";
  if (form.categoryId) form.categoryId.value = p.categoryId ?? "";
  if (form.supplierId) form.supplierId.value = p.supplierId ?? "";

  // Nuevos campos
  if (form.serial) form.serial.value = p.serial ?? "";
  if (form.lote) form.lote.value = p.lote ?? "";
  if (form.dimensiones) form.dimensiones.value = p.dimensiones ?? "";
  if (form.peso) form.peso.value = (p.peso ?? "") === "" || p.peso === null ? "" : p.peso;
  if (form.estadoCertificado) form.estadoCertificado.value = p.estadoCertificado ?? "";
  if (form.observacion) form.observacion.value = p.observacion ?? "";
}

function row(p /** @type {Product} */, term = "") {
  const danger = Number(p.stock) <= Number(p.reorderPoint);
  const sku   = highlight(p.sku, term);
  const name  = highlight(p.name, term);
  const serial = p.serial ? highlight(p.serial, term) : "";
  const lote   = p.lote ? highlight(p.lote, term) : "";

  return `
    <tr class="${danger ? "row-danger": ""}">
      <td>${sku}</td>
      <td>
        <div class="name">${name}</div>
        ${p.barcode ? `<div class="sub">${p.barcode}</div>` : ""}
      </td>
      <td>${p.categoryName ?? "-"}</td>
      <td>${p.supplierName ?? "-"}</td>
      <td class="ta-right">${formatCOP(p.price)}</td>
      <td class="ta-center">${p.stock ?? 0}</td>
      <td class="ta-center">${p.reorderPoint ?? 0}</td>
      <td class="ta-center">${p.status ?? "-"}</td>

      <!-- Nuevos campos -->
      <td>${serial || "-"}</td>
      <td>${lote || "-"}</td>
      <td>${p.dimensiones ?? "-"}</td>
      <td class="ta-right">${(p.peso ?? "") === "" || p.peso === null ? "-" : Number(p.peso).toFixed(3)}</td>
      <td class="ta-center">${p.estadoCertificado ?? "-"}</td>

      <td class="ta-center">
        <button class="btn btn-xs" data-action="edit" data-id="${p.id}">Editar</button>
        <button class="btn btn-xs btn-danger" data-action="del" data-id="${p.id}">Borrar</button>
      </td>
    </tr>
  `;
}

// ======================
// Carga inicial y render
// ======================
async function loadAllToCache() {
  productsCache = await listAll(); // GET a /api/products/showAllProducts
}

function filterByTerm(term) {
  const t = (term || "").trim().toLowerCase();
  if (!t) return productsCache.slice();
  return productsCache.filter(p =>
    String(p.name || "").toLowerCase().includes(t) ||
    String(p.sku  || "").toLowerCase().includes(t) ||
    String(p.serial || "").toLowerCase().includes(t) ||
    String(p.lote || "").toLowerCase().includes(t)
  );
}

function renderList(list) {
  if (!tbody) return;
  const sorted = applySort(list);
  tbody.innerHTML = sorted.map(p => row(p, currentTerm)).join("");
  if (reCount) {
    const count = sorted.filter(p => Number(p.stock) <= Number(p.reorderPoint)).length;
    reCount.textContent = String(count);
  }
}

async function renderAll() {
  if (!productsCache.length) {
    await loadAllToCache();
  }
  const list = filterByTerm(currentTerm);
  renderList(list);
}

// ======================
// Dropdowns categorías / proveedores
// ======================
async function loadCategoriesSelect(selectedId = null) {
  if (!selCat) return;
  selCat.disabled = true;
  selCat.innerHTML = `<option value="" disabled selected>Cargando categorías…</option>`;
  try {
    const r = await fetch(`${apiUrl}/categories/showCategorysByNames`);
    if (!r.ok) throw new Error(`HTTP ${r.status}`);
    const data = await r.json();
    selCat.innerHTML =
      `<option value="" disabled ${selectedId ? "" : "selected"}>-- Selecciona una categoría --</option>` +
      data.map(c =>
        `<option value="${c.id}" ${selectedId && String(selectedId)===String(c.id) ? "selected": ""}>${c.name}</option>`
      ).join("");
  } catch (e) {
    console.error("loadCategoriesSelect", e);
    selCat.innerHTML = `<option value="" disabled selected>Error cargando categorías</option>`;
  } finally {
    selCat.disabled = false;
  }
}

// proveedores
async function loadSuppliersSelect(selectedId = null) {
  if (!selSup) return;
  selSup.disabled = true;
  selSup.innerHTML = `<option value="" disabled selected>Cargando proveedores…</option>`;
  try {
    const r = await fetch(`${apiUrl}/suppliers/showSuppliersByName`);
    if (!r.ok) throw new Error(`HTTP ${r.status}`);
    const data = await r.json();
    selSup.innerHTML =
      `<option value="" disabled ${selectedId ? "" : "selected"}>-- Selecciona un proveedor --</option>` +
      data.map(s =>
        `<option value="${s.id}" ${selectedId && String(selectedId)===String(s.id) ? "selected": ""}>${s.name}</option>`
      ).join("");
  } catch (e) {
    console.error("loadSuppliersSelect", e);
    selSup.innerHTML = `<option value="" disabled selected>Error cargando proveedores</option>`;
  } finally {
    selSup.disabled = false;
  }
}

// ======================
// Reorder
// ======================
if (modeSel && thrInp) {
  modeSel.addEventListener("change", () => {
    const isGlobal = modeSel.value === "global";
    thrInp.disabled = !isGlobal;
    if (!isGlobal) thrInp.value = "";
  });
}
if (btnReor) {
  btnReor.addEventListener("click", async () => {
    try {
      const mode = modeSel ? modeSel.value : "own";
      const thr  = thrInp && thrInp.value ? Number(thrInp.value) : null;
      const data = await (mode === "global" ? listReorder(thr) : listReorder());
      renderList(data); // no tocamos cache original
    } catch (e) {
      toast(e.message, false);
    }
  });
}
if (btnRef) {
  btnRef.addEventListener("click", async () => {
    try {
      await loadAllToCache();
      await renderAll();
    } catch (e) { toast(e.message, false); }
  });
}
if (btnClr) {
  btnClr.addEventListener("click", () => clearForm());
}

// ======================
// Acciones tabla
// ======================
if (tbody) {
  tbody.addEventListener("click", async (e) => {
    const btn = e.target.closest("button[data-action]");
    if (!btn) return;
    const id = btn.getAttribute("data-id");

    if (btn.dataset.action === "edit") {
      const p = productsCache.find(x => String(x.id) === String(id));
      if (p) fillForm(p);
    }

    if (btn.dataset.action === "del") {
      if (!confirm("¿Eliminar producto?")) return;
      try {
        await remove(id);
        productsCache = productsCache.filter(x => String(x.id) !== String(id));
        await renderAll();
        toast("Producto eliminado");
      } catch (e2) {
        toast(e2.message, false);
      }
    }
  });
}

// ======================
// Submit create / update
// ======================
if (form) {
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    if (isSubmitting) return;

    const raw = Object.fromEntries(new FormData(form).entries());

    const product = new Product({
      id: raw.id ? Number(raw.id) : null,
      sku: (raw.sku || "").trim().toUpperCase(),
      barcode: (raw.barcode || "").trim() || null,
      name: (raw.name || "").trim(),
      description: (raw.description || "").trim(),
      price: Number(raw.price),
      reorderPoint: Number(raw.reorderPoint),
      status: String(raw.status || "ACTIVE").toUpperCase(),
      categoryId: raw.categoryId ? Number(raw.categoryId) : null,
      supplierId: raw.supplierId ? Number(raw.supplierId) : null,

      // Nuevos campos
      serial: (raw.serial || "").trim() || null,
      lote: (raw.lote || "").trim() || null,
      dimensiones: (raw.dimensiones || "").trim() || null,
      peso: raw.peso !== "" && raw.peso != null ? Number(raw.peso) : null,
      estadoCertificado: (raw.estadoCertificado || "").trim().toUpperCase() || null,
      observacion: (raw.observacion || "").trim() || null,
    });

    if (!product.categoryId || !product.supplierId) {
      toast("Selecciona categoría y proveedor", false);
      return;
    }

    beginSubmit();
    try {
      if (product.id) {
        const updated = await update(product.id, product.toInsertDto());
        productsCache = productsCache.map(p => String(p.id) === String(updated.id) ? updated : p);
        toast("Producto actualizado");
      } else {
        const created = await create(product.toInsertDto());
        productsCache.push(created);
        toast("Producto creado");
      }
      clearForm();
      await renderAll();
    } catch (err) {
      toast(err.message || "Error guardando el producto", false);
    } finally {
      endSubmit();
    }
  });
}

// ======================
// CSV
// ======================
function toCsvValue(value) {
  const s = value == null ? "" : String(value);
  const needsQuotes = /[",\n]/.test(s);
  const escaped = s.replace(/"/g, '""');
  return needsQuotes ? `"${escaped}"` : escaped;
}

function exportCsvFrom(list) {
  const headers = [
    "id","sku","name","barcode","category","supplier",
    "price","stock","reorderPoint","status",
    "serial","lote","dimensiones","peso","estadoCertificado","observacion"
  ];
  const rows = list.map(p => [
    p.id,
    p.sku,
    p.name,
    p.barcode ?? "",
    p.categoryName ?? "",
    p.supplierName ?? "",
    p.price,
    p.stock ?? 0,
    p.reorderPoint ?? 0,
    p.status ?? "",
    p.serial ?? "",
    p.lote ?? "",
    p.dimensiones ?? "",
    (p.peso == null || p.peso === "") ? "" : Number(p.peso),
    p.estadoCertificado ?? "",
    p.observacion ?? ""
  ]);

  const lines = [
    headers.map(toCsvValue).join(","),
    ...rows.map(r => r.map(toCsvValue).join(","))
  ];
  const csv = lines.join("\n");

  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const url  = URL.createObjectURL(blob);
  const a    = document.createElement("a");
  const now  = new Date();
  const stamp = now.toISOString().replace(/[:T]/g,"-").slice(0,16);
  a.href = url;
  a.download = `products_${stamp}.csv`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

const btnExport = document.getElementById("btn-export-csv");
if (btnExport) {
  btnExport.addEventListener("click", () => {
    const filtered = filterByTerm(currentTerm);
    const sorted   = applySort(filtered);
    exportCsvFrom(sorted);
  });
}

// ======================
// Buscador en vivo
// ======================
if (searchInp) {
  const onType = debounce(async () => {
    currentTerm = searchInp.value || "";
    await renderAll();
  }, 300);
  searchInp.addEventListener("input", onType);

  // Enter: editar primer match
  searchInp.addEventListener("keyup", (e) => {
    if (e.key === "Enter") {
      const list = filterByTerm(searchInp.value || "");
      if (list.length) fillForm(list[0]);
    }
    if (e.key === "Escape") {
      searchInp.value = "";
      currentTerm = "";
      renderAll();
    }
  });
}

if (btnSearch && searchInp) {
  btnSearch.addEventListener("click", () => {
    currentTerm = searchInp.value || "";
    renderAll();
  });
}

if (btnClearSrh && searchInp) {
  btnClearSrh.addEventListener("click", () => {
    searchInp.value = "";
    currentTerm = "";
    renderAll();
  });
}

// ======================
// Init
// ======================
(async () => {
  try {
    await Promise.all([ loadCategoriesSelect(), loadSuppliersSelect() ]);
    await renderAll();
    attachSortHandlers();
    setHeaderSortUI();
  } catch (err) {
    toast(err.message || "Error inicializando página", false);
  }
})();


