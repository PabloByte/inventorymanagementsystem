package com.springweb.catalog.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springweb.catalog.dto.CategoryDtoForHtmlInsert;
import com.springweb.catalog.dto.CategoryDtoInsert;
import com.springweb.catalog.dto.CategoryDtoReturn;
import com.springweb.catalog.service.CategoryServiceImpl;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

  private final CategoryServiceImpl categoryServiceImpl;

  public CategoryController(CategoryServiceImpl categoryServiceImpl) {
    this.categoryServiceImpl = categoryServiceImpl;
  }



  @GetMapping("/showCategorysByNames")
  ResponseEntity<?> showCategorysByNames(){

    java.util.List <CategoryDtoForHtmlInsert> categoryList = categoryServiceImpl.showCategorysByName();

    return ResponseEntity.status(HttpStatus.OK).body(categoryList);
  }

  @GetMapping("/showAllCategorys")
  ResponseEntity<List<CategoryDtoReturn>> showAllCategorys(){

    List<CategoryDtoReturn> list = categoryServiceImpl.allCategorys();

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(list);


  }

  @PostMapping("/createCategory")
  ResponseEntity<?> createCategory (@RequestBody CategoryDtoInsert category ){

    CategoryDtoReturn newCategory = categoryServiceImpl.createCategory(category);

      return ResponseEntity.status(HttpStatus.ACCEPTED).body(newCategory);
  };


    @PutMapping("/updateCategory/{id}")
  public ResponseEntity<CategoryDtoReturn> updateCategory(@PathVariable Long id, @RequestBody CategoryDtoInsert category) {

    CategoryDtoReturn updated = categoryServiceImpl.updateCategory(id, category);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(updated);
  }



  

  

   
   























}
