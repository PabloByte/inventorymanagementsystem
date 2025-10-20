package com.springweb.catalog.dto;

import java.util.List;

public class CategoryDtoReturn {

    private Long id;
    
   
    private String name;
    
    private String description;
    
   
    private List<ProductDtoReturn> products;


    public CategoryDtoReturn(Long id, String name, String description, List<ProductDtoReturn> products) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.products = products;
    }


    public CategoryDtoReturn() {
    }


    public Long getId() {
      return id;
    }


    public void setId(Long id) {
      this.id = id;
    }


    public String getName() {
      return name;
    }


    public void setName(String name) {
      this.name = name;
    }


    public String getDescription() {
      return description;
    }


    public void setDescription(String description) {
      this.description = description;
    }


    public List<ProductDtoReturn> getProducts() {
      return products;
    }


    public void setProducts(List<ProductDtoReturn> products) {
      this.products = products;
    }

    






}
