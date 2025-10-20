package com.springweb.catalog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springweb.catalog.domain.Category;
import com.springweb.catalog.dto.CategoryDtoForHtmlInsert;
import com.springweb.catalog.dto.CategoryDtoInsert;
import com.springweb.catalog.dto.CategoryDtoReturn;
import com.springweb.catalog.repo.CategoryRepository;
import com.springweb.shared.mapper.InventoryManagementMapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryServiceImpl implements  IcategoryService {

private final CategoryRepository categoryRepository;
private final InventoryManagementMapper mapper ;

public CategoryServiceImpl(CategoryRepository categoryRepository, InventoryManagementMapper mapper) {
  this.categoryRepository = categoryRepository;
  this.mapper = mapper;
}



  @Override
  public CategoryDtoReturn createCategory(CategoryDtoInsert category) {

     Category newCategory = new  Category();

     newCategory.setDescription(category.getDescription());
     newCategory.setName(category.getName());

    categoryRepository.save(newCategory);

    return  mapper.categoryToCategoryDtoReturn(newCategory);

  }

   @Override
   @Transactional
  public CategoryDtoReturn updateCategory(Long id,CategoryDtoInsert category) {


      if (id == null) {
    throw new IllegalArgumentException("El id de la categoría es obligatorio.");
  }
  if (category == null) {
    throw new IllegalArgumentException("El payload de la categoría es obligatorio.");
  }

  final String name = category.getName() == null ? "" : category.getName().trim();
  if (name.isEmpty()) {
    throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
  }

    // 1) Buscar existente (en la sesión)
    Category entity = categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id=" + id));

    // (Opcional) Validar unicidad si aplica
    // if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
    //   throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + name);
    // }

    // 2) Actualizar campos
    entity.setName(name);
    entity.setDescription(category.getDescription() == null ? null : category.getDescription().trim());

    // 3) Guardar
    categoryRepository.save(entity);

    // 4) Releer con fetch join (misma transacción => colecciones inicializadas)
    Category hydrated = categoryRepository.findByIdWithProductsAndSuppliers(id)
        .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada (post-update) con id=" + id));

    // 5) Mapear a DTO
    return mapper.categoryToCategoryDtoReturn(hydrated);
  

  }


  

  @Override
  public List<CategoryDtoReturn> allCategorys() {


    List<Category> list = categoryRepository.findAllWithProductsAndSuppliers();

    return mapper.categoryListToCategoryDtoReturnsList(list);
    
  }
















  @Override
  public List<Category> findByNameContainingIgnoreCase(String name) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findByNameContainingIgnoreCase'");
  }

  @Override
  public Optional<Category> findByName(String name) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findByName'");
  }






  //ESTE LO USO PARA MOSTRAR LAS LISTAS EN HTML SELECCIONABLES
  @Override
  public List<CategoryDtoForHtmlInsert> showCategorysByName() {

    List <CategoryDtoForHtmlInsert> showCategoryNames = categoryRepository.showCategorysByName();

    return showCategoryNames;
  
  }



 

}
