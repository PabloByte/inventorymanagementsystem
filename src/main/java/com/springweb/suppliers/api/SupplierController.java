package com.springweb.suppliers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springweb.suppliers.dto.SupplierDtoForHtmlInsert;
import com.springweb.suppliers.dto.SupplierDtoInsert;
import com.springweb.suppliers.dto.SupplierDtoReturn;
import com.springweb.suppliers.service.SupplierServiceImpl;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {


    private final SupplierServiceImpl supplierServiceImpl;

    public SupplierController(SupplierServiceImpl supplierServiceImpl) {
        this.supplierServiceImpl = supplierServiceImpl;
    }


    @PostMapping("/createSupplier")
    ResponseEntity<SupplierDtoReturn> createSupplier (@RequestBody SupplierDtoInsert dtoInsert){

        SupplierDtoReturn newSupplier = supplierServiceImpl.createSupplier(dtoInsert);

        return ResponseEntity.status(HttpStatus.CREATED).body(newSupplier);

    }










    @GetMapping("/showSuppliersByName")
    ResponseEntity<?> showSuppliersByName (){


        List<SupplierDtoForHtmlInsert> list = supplierServiceImpl.showSupplierByName();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(list);

    }












    
}
