// /js/services/CategoryService.js
import { apiUrl } from "/js/config/env.js";
import Category from "../entities/category.js"; // default export; también puedes: import { Category } from "../entities/category.js";

export class CategoryService {
  constructor() {}

  async #request(path, options = {}) {
    const url = `${apiUrl}${path}`;
    const resp = await fetch(url, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
    // Tu backend usa 202 Accepted en showAllCategorys; lo aceptamos como válido
    if (!resp.ok && resp.status !== 202) {
      const text = await resp.text().catch(() => "");
      throw new Error(`HTTP ${resp.status} ${resp.statusText} - ${text}`);
    }
    const ct = resp.headers.get("content-type") || "";
    if (!ct.includes("application/json")) return null;
    return resp.json();
  }

  // READ — listar todas las categorías con productos
  async getAllCategories() {
    try {
      const data = await this.#request(`/categories/showAllCategorys`, { method: "GET" });
      if (!Array.isArray(data)) return [];
      return data.map(Category.fromDto);
    } catch (err) {
      console.error("[CategoryService] getAllCategories error:", err);
      return [];
    }
  }

  // CRUD listos para cuando habilites endpoints
  async getCategoryById(id) {
    const data = await this.#request(`/categories/${encodeURIComponent(id)}`, { method: "GET" });
    return data ? Category.fromDto(data) : null;
  }

  async createCategory(category /* Category */) {
    const errs = category.validateBasic?.() ?? [];
    if (errs.length) throw new Error(errs.join(" / "));

    //payload es el objeto que se envía al backend
   const dto = category.toDto({ includeProducts: false });
   const payload = {name: dto.name, description: dto.description ?? ""};

   const data = await this.#request(`/categories/createCategory`, {

    method: "POST",
    body: JSON.stringify(payload),
   });

    return data ? Category.fromDto(data) : null;
  }

 async updateCategory(id, category /* Category */) {
  if (id == null) throw new Error("Falta el id de la categoría.");

  const errs = category.validateBasic?.() ?? [];
  if (errs.length) throw new Error(errs.join(" / "));

  const dto = category.toDto({ includeProducts: false });
  const payload = { name: dto.name, description: dto.description ?? "" };

  const data = await this.#request(`/categories/updateCategory/${encodeURIComponent(id)}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });

  return data ? Category.fromDto(data) : null;
}

  

  

  async deleteCategory(id) {
    await this.#request(`/categories/${encodeURIComponent(id)}`, { method: "DELETE" });
    return true;
  }
}
