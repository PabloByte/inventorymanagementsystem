package com.springweb.suppliers.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springweb.suppliers.domain.Supplier;
import com.springweb.suppliers.dto.SupplierDtoForHtmlInsert;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {


    @Query("SELECT new com.springweb.suppliers.dto.SupplierDtoForHtmlInsert(s.id, s.name) FROM Supplier s ORDER BY s.name ASC")
    List<SupplierDtoForHtmlInsert> showSupplierByName();
    
    List<Supplier> findByNameContainingIgnoreCase(String name);
    
    List<Supplier> findByEmailContainingIgnoreCase(String email);
}
