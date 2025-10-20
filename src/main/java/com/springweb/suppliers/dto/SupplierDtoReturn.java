package com.springweb.suppliers.dto;

import java.util.List;

import com.springweb.catalog.dto.ProductDtoReturn;

public class SupplierDtoReturn {

   private Long id;

   private String name;
    
    private String contactPerson;
    
    private String email;
    
    private String phone;
    
    private String address;

     private List<ProductDtoReturn> products;

     public SupplierDtoReturn(Long id, String name, String contactPerson, String email, String phone, String address,
        List<ProductDtoReturn> products) {
      this.id = id;
      this.name = name;
      this.contactPerson = contactPerson;
      this.email = email;
      this.phone = phone;
      this.address = address;
      this.products = products;
     }

     public SupplierDtoReturn() {
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

     public String getContactPerson() {
       return contactPerson;
     }

     public void setContactPerson(String contactPerson) {
       this.contactPerson = contactPerson;
     }

     public String getEmail() {
       return email;
     }

     public void setEmail(String email) {
       this.email = email;
     }

     public String getPhone() {
       return phone;
     }

     public void setPhone(String phone) {
       this.phone = phone;
     }

     public String getAddress() {
       return address;
     }

     public void setAddress(String address) {
       this.address = address;
     }

     public List<ProductDtoReturn> getProducts() {
       return products;
     }

     public void setProducts(List<ProductDtoReturn> products) {
       this.products = products;
     }


     





}
