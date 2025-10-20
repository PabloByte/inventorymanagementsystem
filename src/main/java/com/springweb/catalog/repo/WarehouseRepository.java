package com.springweb.catalog.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.springweb.catalog.domain.Warehouse;
import com.springweb.catalog.dto.WarehouseDtoForHtmlSelect;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {


Optional<Warehouse> findByCode(String code);


  @Query("""
SELECT new com.springweb.catalog.dto.WarehouseDtoForHtmlSelect( w.id, w.name, w.code, w.address)
           from Warehouse w
           where w.active = true
           order by w.name asc
           """)
    List<WarehouseDtoForHtmlSelect> findActiveForHtmlSelect();









        

}
