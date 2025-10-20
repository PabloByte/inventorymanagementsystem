package com.springweb.catalog.dto;




public class WarehouseDtoForHtmlSelect {


    private  Long id;
    private  String name;
    private  String code;
    private  String address;

    public WarehouseDtoForHtmlSelect(Long id, String name, String code, String address) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getAddress() {
        return address;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setAddress(String address) {
        this.address = address;
    }

   

    


}

