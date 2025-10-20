package com.springweb.catalog.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springweb.catalog.dto.WarehouseDtoForHtmlSelect;
import com.springweb.catalog.service.WarehouseServiceImpl;

@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin(origins = "*")
public class WarehouseController {
        
    private final WarehouseServiceImpl warehouseService;

    public WarehouseController(WarehouseServiceImpl warehouseService) {
        this.warehouseService = warehouseService;
}



    // GET /api/warehouses/showByName  →  [{ id, name, code, address }]
    @GetMapping("/showByName")
    public ResponseEntity<List<WarehouseDtoForHtmlSelect>> showByName() {
        List<WarehouseDtoForHtmlSelect> list = warehouseService.listForHtmlSelect();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }













}
