package com.springweb.catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.springweb.catalog.dto.WarehouseDtoForHtmlSelect;
import com.springweb.catalog.repo.WarehouseRepository;

@Service
public class WarehouseServiceImpl implements  IWarehouseService {

        private final WarehouseRepository warehouseRepository;

        public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
                this.warehouseRepository = warehouseRepository;
        }



        @Override
        public List<WarehouseDtoForHtmlSelect> listForHtmlSelect() {

            return warehouseRepository.findActiveForHtmlSelect(); //

        }



        



}
