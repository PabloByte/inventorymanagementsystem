// /js/category.js
// UI de Categorías: pinta tabla principal y detalle de productos

const $ = (sel, ctx = document) => ctx.querySelector(sel);

function clearTable() {
  const tbody = $("#categoriesTable tbody");
  if (tbody) tbody.innerHTML = "";
}
function showEmpty(state) {
  const el = $("#category-empty");
  if (el) el.style.display = state ? "" : "none";
}

function renderCategoryRow(cat) {
  const tpl = $("#tpl-category-row");
  const tr = tpl.content.firstElementChild.cloneNode(true);
  tr.dataset.id = cat.id;
  tr.querySelector(".cat-id").textContent = cat.id ?? "—";
  tr.querySelector(".cat-name").textContent = cat.name ?? "—";
  tr.querySelector(".cat-desc").textContent = cat.description ?? "—";
  tr.querySelector(".cat-products-count").textContent = (cat.products?.length ?? 0);
  return tr;
}

function renderProductsRow(cat) {
  const tpl = $("#tpl-category-products");
  const tr = tpl.content.firstElementChild.cloneNode(true);
  tr.dataset.id = cat.id;
  const list = tr.querySelector(".products-list");
  const prods = cat.products ?? [];

  if (!prods.length) {
    const span = document.createElement("span");
    span.className = "muted";
    span.textContent = "Sin productos";
    list.appendChild(span);
    return tr;
  }

  // Mini-tabla con todas las columnas requeridas
  const table = document.createElement("table");
  table.className = "table table-sm";
  table.innerHTML = `
    <thead>
      <tr>
        <th>ID</th><th>Nombre</th><th>Descripción</th><th>Precio</th>
        <th>Stock</th><th>Proveedor</th><th>Serial</th><th>Lote</th>
        <th>Estado Certificado</th><th>Observación</th>
      </tr>
    </thead>
    <tbody></tbody>
  `;
  const tbody = table.querySelector("tbody");

  const fmtMoney = (v) => {
    if (v === null || v === undefined || v === "") return "—";
    const n = Number(v); if (Number.isNaN(n)) return String(v);
    try { return new Intl.NumberFormat("es-CO",{style:"currency",currency:"COP",maximumFractionDigits:0}).format(n); }
    catch { return n.toFixed(2); }
  };
  const safe = (v) => (v === null || v === undefined || v === "" ? "—" : String(v));

  prods.forEach(p => {
    const trP = document.createElement("tr");
    [
      p.id, p.name, p.description, fmtMoney(p.price), p.stock,
      p.supplierName, p.serial, p.lote, p.estadoCertificado, p.observacion
    ].forEach(val => {
      const td = document.createElement("td");
      td.textContent = safe(val);
      trP.appendChild(td);
    });
    tbody.appendChild(trP);
  });

  list.innerHTML = "";
  list.appendChild(table);
  return tr;
}

function bindToggleBehavior(row, detailRow) {
  const btn = row.querySelector(".btn-toggle");
  const icon = btn.querySelector("i");
  let opened = false;
  btn.addEventListener("click", () => {
    opened = !opened;
    detailRow.style.display = opened ? "" : "none";
    icon.classList.toggle("fa-chevron-right", !opened);
    icon.classList.toggle("fa-chevron-down", opened);
  });
}

function renderList(list = []) {
  const tbody = $("#categoriesTable tbody");
  if (!tbody) return;
  clearTable();

  if (!list.length) {
    showEmpty(true);
    tbody.innerHTML = `<tr><td colspan="6">—</td></tr>`;
    return;
  }
  showEmpty(false);

  list.forEach(cat => {
    const row = renderCategoryRow(cat);
    const productsRow = renderProductsRow(cat);
    productsRow.style.display = "none";
    tbody.appendChild(row);
    tbody.appendChild(productsRow);
    bindToggleBehavior(row, productsRow);
  });
}

function wireHeaderButtons() {
  $("#btnRefreshCategories")?.addEventListener("click", () => {
    document.dispatchEvent(new CustomEvent("categories:refresh"));
  });
  $("#btnNewCategory")?.addEventListener("click", () => {
    document.dispatchEvent(new CustomEvent("categories:new"));
  });
}

function ensureOnce() {
  if (ensureOnce._done) return;
  ensureOnce._done = true;
  wireHeaderButtons();
}

window.CategoriesUI = {
  render: (dtoList) => { ensureOnce(); renderList(dtoList); },
  clear: () => { clearTable(); showEmpty(true); }
};



