// /js/entities/category.js
import Product from "./Product.js";

export class Category {
  constructor({ id = null, name = "", description = "", products = [] } = {}) {
    this.id = id;
    this.name = name;
    this.description = description;
    // products como array de Entities Product
    this.products = Array.isArray(products) ? products : [];
  }

  // DTO backend -> Entity
  static fromDto(dto = {}) {
    return new Category({
      id: dto.id ?? null,
      name: dto.name ?? "",
      description: dto.description ?? "",
      products: Array.isArray(dto.products) ? dto.products.map(Product.fromDto) : [],
    });
  }

  // Entity -> DTO backend (para create/update de categoría)
  // Nota: si el backend por ahora NO recibe productos en create/update de categoría,
  // puedes enviar solo name/description. Lo dejamos extensible.
  toDto({ includeProducts = false } = {}) {
    const base = {
      id: this.id,
      name: this.name,
      description: this.description,
    };
    if (includeProducts) {
      base.products = this.products.map(p => p.toInsertDto());
    }
    return base;
  }

  // Entity -> plano para la UI (lo que pinta la tabla)
toJSON() {
  return {
    id: this.id,
    name: this.name,
    description: this.description,
    products: this.products.map(p =>
      typeof p.toJSON === "function" ? p.toJSON() : p
    ),
  };
}

  validateBasic() {
    const errors = [];
    if (!this.name || !this.name.trim()) errors.push("El nombre de la categoría es obligatorio.");
    return errors;
  }

  setProductsFromDtos(dtoList = []) {
    this.products = Array.isArray(dtoList) ? dtoList.map(Product.fromDto) : [];
  }
}

export default Category;


