package com.springweb.catalog.service.admin;

import org.springframework.stereotype.Service;

import com.springweb.catalog.repo.ProductRepository;
import com.springweb.catalog.repo.StockRepository;
import com.springweb.catalog.repo.WarehouseRepository;
import com.springweb.catalog.service.InventoryServiceImpl;

import jakarta.transaction.Transactional;

@Service
public class InventoryAdminService {

         private final WarehouseRepository warehouseRepo;
  private final ProductRepository productRepo;
  private final StockRepository stockRepo;
  private final InventoryServiceImpl inventoryService;

  public InventoryAdminService(
      WarehouseRepository warehouseRepo,
      ProductRepository productRepo,
      StockRepository stockRepo,
      InventoryServiceImpl inventoryService) {
    this.warehouseRepo = warehouseRepo;
    this.productRepo = productRepo;
    this.stockRepo = stockRepo;
    this.inventoryService = inventoryService;
  }

  @Transactional
  public void backfillMainFromProductStock(String createdBy) {
    var main = warehouseRepo.findByCode("MAIN")
        .orElseThrow(() -> new IllegalStateException("MAIN warehouse not found"));

    productRepo.findAll().forEach(p -> {
      Integer currentTotal = stockRepo.sumQuantityByProductId(p.getId());
      int total = currentTotal != null ? currentTotal : 0;
      int desired = p.getStock() != null ? p.getStock() : 0;
      int delta = desired - total; // lo que falta para igualar el total al campo product.stock (cache actual)

      if (delta != 0) {
        inventoryService.adjustStock(
            p.getId(),
            main.getId(),
            delta,
            "ADJUSTMENT",
            "BACKFILL-"+p.getId(),
            "Backfill inicial desde Product.stock",
            createdBy != null ? createdBy : "system"
        );
      }
    });
  }




}
