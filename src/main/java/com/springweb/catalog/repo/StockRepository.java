package com.springweb.catalog.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springweb.catalog.domain.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {


Optional<Stock> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

@Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.product.id = :productId")
Integer sumQuantityByProductId(@Param("productId") Long productId);



 
}
