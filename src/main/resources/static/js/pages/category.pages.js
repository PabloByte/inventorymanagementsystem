// /js/pages/category.pages.js
import { CategoryService } from "../services/CategoryService.js";
import Category from "../entities/category.js";

const service = new CategoryService();

function setLoading(on) {
  const tbody = document.querySelector("#categoriesTable tbody");
  if (tbody && on) tbody.innerHTML = `<tr><td colspan="6">Cargando...</td></tr>`;
}
function toast(m, t = "info") {
  console[t === "error" ? "error" : "log"]("[Categories]", m);
}

// ==============================
// [MODIFICADO]  Carga + render
// ==============================
export async function loadAndRenderCategories() {
  try {
    setLoading(true);
    const entities = await service.getAllCategories(); // [Category]
    const dtoLike = entities.map((e) => e.toJSON()); // UI recibe planos
    window.CategoriesUI?.render?.(dtoLike);
  } catch (e) {
    console.error(e);
    toast("No se pudieron cargar las categorías", "error");
    window.CategoriesUI?.clear?.();
  } finally {
    setLoading(false);
  }
}

// ==============================
// [MODIFICADO]  Eventos globales
// ==============================
function wireGlobalEvents() {
  document.addEventListener("categories:refresh", loadAndRenderCategories);
  // Antes era un stub: ahora llama al flujo real de creación
  document.addEventListener("categories:new", onNewCategory); // [MODIFICADO]
}

// =======================================================
// [NUEVO] Utilidades para capturar datos (modal o prompt)
// =======================================================
function getModalElements() {
  // Si más adelante creas un modal real, ajusta aquí los selectores:
  const modal = document.getElementById("categoryModal");
  const form = document.getElementById("categoryForm");
  const inputName = document.getElementById("catName");
  const inputDesc = document.getElementById("catDesc");
  const btnSave = document.getElementById("btnSaveCategory");
  const btnCancel = document.getElementById("btnCancelCategory");
  return { modal, form, inputName, inputDesc, btnSave, btnCancel };
}

function openModalIfExists({ title = "Categoría", name = "", description = "" } = {}) {
  const els = getModalElements();
  if (!els.modal || !els.form || !els.inputName || !els.inputDesc) return null;

  // Simple init (sin estilos; ajusta a tu modal)
  els.modal.style.display = "flex";
  const titleEl = els.modal.querySelector("[data-title]");
  if (titleEl) titleEl.textContent = title;
  els.inputName.value = name ?? "";
  els.inputDesc.value = description ?? "";
  return els;
}

function closeModalIfExists() {
  const { modal, form } = getModalElements();
  if (form) form.reset();
  if (modal) modal.style.display = "none";
}

function readFormOrPrompt(defaults = { name: "", description: "" }) {
  // 1) Si hay modal en DOM, retorna función que lee desde los inputs
  const els = openModalIfExists({
    title: defaults.title || "Categoría",
    name: defaults.name || "",
    description: defaults.description || "",
  });
  if (els) {
    return new Promise((resolve, reject) => {
      const { form, inputName, inputDesc, btnSave, btnCancel } = els;

      const onSubmit = (e) => {
        e.preventDefault();
        const payload = {
          name: (inputName.value || "").trim(),
          description: (inputDesc.value || "").trim(),
        };
        form.removeEventListener("submit", onSubmit);
        btnCancel?.removeEventListener("click", onCancel);
        closeModalIfExists();
        resolve(payload);
      };
      const onCancel = (e) => {
        e.preventDefault();
        form.removeEventListener("submit", onSubmit);
        btnCancel?.removeEventListener("click", onCancel);
        closeModalIfExists();
        reject(new Error("cancelled"));
      };

      form.addEventListener("submit", onSubmit);
      btnCancel?.addEventListener("click", onCancel);
      btnSave?.removeAttribute?.("disabled");
    });
  }

  // 2) Fallback: prompt()
  return new Promise((resolve, reject) => {
    const name = window.prompt("Nombre de la categoría:", defaults.name || "");
    if (name === null) return reject(new Error("cancelled"));
    const description =
      window.prompt("Descripción (opcional):", defaults.description || "") || "";
    resolve({
      name: String(name || "").trim(),
      description: String(description || "").trim(),
    });
  });
}

// =====================================
// [NUEVO]  Crear / Editar / Eliminar
// =====================================
async function onNewCategory() {
  try {
    const { name, description } = await readFormOrPrompt({
      title: "Nueva categoría",
      name: "",
      description: "",
    });

    const cat = new Category({ name, description });
    const errors = cat.validateBasic();
    if (errors.length) {
      toast(errors.join(" / "), "error");
      return;
    }

    await service.createCategory(cat);
    toast("Categoría creada.");
    await loadAndRenderCategories();
  } catch (err) {
    if (err?.message === "cancelled") return; // usuario canceló
    console.error(err);
    toast(parseApiError(err) || "No se pudo crear la categoría.", "error");
  }
}

async function onEditCategory(id) {
  try {
    // Si tu backend tiene GET /categories/{id}, úsalo:
    // const current = await service.getCategoryById(id);
    // const base = current?.toJSON?.() ?? { id, name: "", description: "" };

    // Mientras no haya GET by id, pedimos inline (puedes precargar desde la fila si quieres):
    const base = { id, name: "", description: "" };

    const { name, description } = await readFormOrPrompt({
      title: `Editar categoría #${id}`,
      name: base.name,
      description: base.description,
    });

    const cat = new Category({ id: Number(id), name, description });
    const errors = cat.validateBasic();
    if (errors.length) {
      toast(errors.join(" / "), "error");
      return;
    }

    await service.updateCategory(id, cat);
    toast(`Categoría #${id} actualizada.`);
    await loadAndRenderCategories();
  } catch (err) {
    if (err?.message === "cancelled") return;
    console.error(err);
    toast(parseApiError(err) || "No se pudo actualizar la categoría.", "error");
  }
}

async function onDeleteCategory(id) {
  if (!confirm(`¿Eliminar la categoría #${id}?`)) return;
  try {
    await service.deleteCategory(id);
    toast(`Categoría #${id} eliminada.`);
    await loadAndRenderCategories();
  } catch (err) {
    console.error(err);
    toast(parseApiError(err) || "No se pudo eliminar la categoría.", "error");
  }
}

// =====================================
// [NUEVO]  Delegación de acciones tabla
// =====================================
function wireTableActions() {
  const table = document.getElementById("categoriesTable");
  if (!table) return;

  table.addEventListener("click", async (ev) => {
    const btnEdit = ev.target.closest(".btn-edit");
    const btnDelete = ev.target.closest(".btn-delete");
    const row = ev.target.closest("tr.cat-row");
    const id = row?.dataset?.id;

    if (btnEdit && id) {
      ev.preventDefault();
      await onEditCategory(id); // [NUEVO]
    }

    if (btnDelete && id) {
      ev.preventDefault();
      await onDeleteCategory(id); // [NUEVO]
    }
  });
}

// =====================================
// [NUEVO]  Parse de errores del backend
// =====================================
function parseApiError(err) {
  // El helper #request del service lanza Error con texto; aquí puedes parsear JSON si lo incluyes allí.
  // Trata de extraer un mensaje legible.
  const msg = String(err?.message || "");
  // Intenta detectar payloads comunes: {"message":"..."} o plain text
  try {
    const maybeJsonStart = msg.indexOf("{");
    if (maybeJsonStart >= 0) {
      const json = JSON.parse(msg.slice(maybeJsonStart));
      if (json?.message) return json.message;
      if (json?.error) return json.error;
    }
  } catch (_) {}
  return msg.replace(/^HTTP \d+\s+[A-Za-z ]+\s*-\s*/i, "").trim();
}

// =====================================
// Auto-init (sin cambios visibles)
// =====================================
(function init() {
  if (!document.getElementById("categoriesTable")) return;
  wireGlobalEvents();
  wireTableActions();
  loadAndRenderCategories();
})();


