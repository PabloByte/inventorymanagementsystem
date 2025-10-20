package com.springweb.catalog.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springweb.catalog.domain.Category;
import com.springweb.catalog.dto.CategoryDtoForHtmlInsert;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByNameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products p LEFT JOIN FETCH p.supplier")
    List<Category> findAllWithProductsAndSuppliers();


    @Query("SELECT new com.springweb.catalog.dto.CategoryDtoForHtmlInsert(c.id, c.name) FROM Category c ORDER BY c.name ASC")
    List<CategoryDtoForHtmlInsert> showCategorysByName();
    
    Optional<Category> findByName(String name);

     // === Nuevo: fetch join por id para evitar Lazy al devolver DTO en update ===
  @Query("""
         SELECT DISTINCT c
         FROM Category c
         LEFT JOIN FETCH c.products p
         LEFT JOIN FETCH p.supplier s
         WHERE c.id = :id
         """)
  Optional<Category> findByIdWithProductsAndSuppliers(@Param("id") Long id);

    
}
