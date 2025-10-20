package com.springweb.suppliers.service;

import java.util.List;

import com.springweb.suppliers.domain.Supplier;
import com.springweb.suppliers.dto.SupplierDtoForHtmlInsert;
import com.springweb.suppliers.dto.SupplierDtoInsert;
import com.springweb.suppliers.dto.SupplierDtoReturn;

public interface ISupplierService {

      SupplierDtoReturn createSupplier (SupplierDtoInsert supplier );
    

   List<SupplierDtoForHtmlInsert> showSupplierByName();
    
    List<Supplier> findByNameContainingIgnoreCase(String name);
    
    List<Supplier> findByEmailContainingIgnoreCase(String email);




}
