package com.springweb.catalog.service;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.springweb.catalog.domain.Product;
import com.springweb.catalog.dto.ProductDtoForHtmlInsert;
import com.springweb.catalog.dto.ProductDtoInsert;
import com.springweb.catalog.dto.ProductDtoReturn;

public interface IProductService {

   ProductDtoReturn getById(Long id);


  ProductDtoReturn createProduct(ProductDtoInsert product); 
  ProductDtoReturn findProductById (Long id ); 


  List<ProductDtoReturn> showAllProducts ();


 ProductDtoReturn updateProduct(Long id, ProductDtoInsert dto);
void deleteProductById (Long id);

  List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findBySupplierId(Long supplierId);
    
    @Query("SELECT p FROM Product p WHERE p.stock < :threshold")
    List<Product> findLowStockProducts(Integer threshold);


    List<ProductDtoForHtmlInsert> showProductsByName();

    public List<?> listForReorder(Integer thresholdOpt);





}
