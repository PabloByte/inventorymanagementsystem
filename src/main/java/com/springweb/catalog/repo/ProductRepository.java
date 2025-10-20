package com.springweb.catalog.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springweb.catalog.domain.Product;
import com.springweb.catalog.domain.Product.ProductStatus;
import com.springweb.catalog.dto.ProductDtoForHtmlInsert;
import com.springweb.catalog.dto.view.ReorderProductView;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  @EntityGraph(attributePaths = {"category","supplier"})
  Optional<Product> findWithGraphById(Long id);

   // --- Unicidad (para create/update) ---
  boolean existsBySku(String sku);
  boolean existsByBarcode(String barcode);

   // Para UPDATE: verificar unicidad excluyendo el propio id
    boolean existsBySkuAndIdNot(String sku, Long id);
    boolean existsByBarcodeAndIdNot(String barcode, Long id);



    @Query("SELECT new com.springweb.catalog.dto.ProductDtoForHtmlInsert(p.id, p.name) FROM Product p ORDER BY p.name ASC")
    List<ProductDtoForHtmlInsert> showProductByName();
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findBySupplierId(Long supplierId);
    
    @Query("SELECT p FROM Product p WHERE p.stock < :threshold")
    List<Product> findLowStockProducts(Integer threshold);

     //  helpers que ya usas en “reorder global”
    List<Product> findAllByStatusAndStockLessThanEqual (ProductStatus status, Integer stockThreshold ); 



      // ya esta funcionando perfectamente
  @Query("""
    SELECT DISTINCT p
    FROM Product p
    LEFT JOIN FETCH p.category
    LEFT JOIN FETCH p.supplier
  """)
  List<Product> findAllWithCategoryAndSupplier();


  @Query("""
  SELECT DISTINCT p
  FROM Product p
  LEFT JOIN FETCH p.category
  LEFT JOIN FETCH p.supplier
  WHERE p.status = com.springweb.catalog.domain.Product.ProductStatus.ACTIVE
    AND p.stock IS NOT NULL
    AND p.reorderPoint IS NOT NULL
    AND p.stock <= p.reorderPoint
""")
List<Product> findAllActiveBelowOwnReorderPoint();


@Query("""
SELECT
  p.id           AS id,
  p.sku          AS sku,
  p.barcode      AS barcode,
  p.name         AS name,
  p.description  AS description,
  p.price        AS price,
  p.stock        AS stock,
  p.reorderPoint AS reorderPoint,
  CAST(p.status AS string) AS status,
  c.id           AS categoryId,
  c.name         AS categoryName,
  s.id           AS supplierId,
  s.name         AS supplierName,
  p.createdAt    AS createdAt,
  p.updatedAt    AS updatedAt
FROM Product p
JOIN p.category c
JOIN p.supplier s
WHERE p.status = com.springweb.catalog.domain.Product.ProductStatus.ACTIVE
  AND p.stock IS NOT NULL
  AND p.reorderPoint IS NOT NULL
  AND p.stock <= p.reorderPoint
ORDER BY p.stock ASC, p.name ASC
""")
List<ReorderProductView> findReorderByOwnROP();


@Query("""
SELECT
  p.id           AS id,
  p.sku          AS sku,
  p.barcode      AS barcode,
  p.name         AS name,
  p.description  AS description,
  p.price        AS price,
  p.stock        AS stock,
  p.reorderPoint AS reorderPoint,
  CAST(p.status AS string) AS status,
  c.id           AS categoryId,
  c.name         AS categoryName,
  s.id           AS supplierId,
  s.name         AS supplierName,
  p.createdAt    AS createdAt,
  p.updatedAt    AS updatedAt
FROM Product p
JOIN p.category c
JOIN p.supplier s
WHERE p.status = com.springweb.catalog.domain.Product.ProductStatus.ACTIVE
  AND p.stock IS NOT NULL
  AND p.stock <= :threshold
ORDER BY p.stock ASC, p.name ASC
""")
List<ReorderProductView> findReorderByGlobalThreshold(Integer threshold);

    
}
