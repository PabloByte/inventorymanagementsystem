package com.springweb.catalog.dto;

public class CategoryDtoForHtmlInsert {

     private Long id;
    private String name;


    public CategoryDtoForHtmlInsert(Long id, String name) {
      this.id = id;
      this.name = name;
    }


    public CategoryDtoForHtmlInsert() {
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
