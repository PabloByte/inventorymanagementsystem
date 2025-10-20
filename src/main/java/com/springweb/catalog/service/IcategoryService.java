package com.springweb.catalog.service;

import java.util.List;
import java.util.Optional;

import com.springweb.catalog.domain.Category;
import com.springweb.catalog.dto.CategoryDtoForHtmlInsert;
import com.springweb.catalog.dto.CategoryDtoInsert;
import com.springweb.catalog.dto.CategoryDtoReturn;

public interface IcategoryService {

  List<CategoryDtoForHtmlInsert> showCategorysByName();

   CategoryDtoReturn updateCategory (Long id, CategoryDtoInsert category);

 

  CategoryDtoReturn createCategory (CategoryDtoInsert category);

  List<CategoryDtoReturn> allCategorys ();

   List<Category> findByNameContainingIgnoreCase(String name);
    
    Optional<Category> findByName(String name);
















}
