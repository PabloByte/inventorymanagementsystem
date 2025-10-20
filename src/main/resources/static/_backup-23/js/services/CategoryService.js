import { config } from "../../../config.js";
import { Category } from "../entitys/category.js";


export class CategoryService {
    constructor() {}

    // ✅ Lista todas las categorías (solo id, name, description, products opcional)
  async getAllCategories() {
  let categories = [];
  try {
    const response = await fetch(`${config.apiUrl}/categories/showAllCategorys`);
    const data = await response.json();

    data.forEach(item => {
      let category = new Category(item.id, item.name, item.description, item.products);
      categories.push(category);
    });

    return categories;
  } catch (error) {
    console.error("Error al cargar categorías:", error);
    return [];
  }
}

   
    getCategoryById(id) {
        return this.$http.get(`/api/categories/${id}`);
      } 
    createCategory(category) {
        return this.$http.post('/api/categories', category);
      }
    updateCategory(id, category) {
        return this.$http.put(`/api/categories/${id}`, category);
      }
    deleteCategory(id) {
        return this.$http.delete(`/api/categories/${id}`);
      }

    }