package com.springweb.catalog.dto;

public class ProductDtoForHtmlInsert {


    private Long id;

    private String name;

    public ProductDtoForHtmlInsert(Long id, String name) {
      this.id = id;
      this.name = name;
    }

    public ProductDtoForHtmlInsert() {
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
    










}
