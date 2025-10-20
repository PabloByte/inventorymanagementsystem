package com.springweb.repository;

import com.springweb.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    List<Supplier> findByNameContainingIgnoreCase(String name);
    
    List<Supplier> findByEmailContainingIgnoreCase(String email);
}
