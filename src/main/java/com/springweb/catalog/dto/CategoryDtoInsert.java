package com.springweb.catalog.dto;

public class CategoryDtoInsert {

    private String name;
    
    private String description;

    public CategoryDtoInsert(String name, String description) {
      this.name = name;
      this.description = description;
    }

    public CategoryDtoInsert() {
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

    
    









}
