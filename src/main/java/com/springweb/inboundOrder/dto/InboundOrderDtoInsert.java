package com.springweb.inboundOrder.dto;

import java.util.ArrayList;
import java.util.List;

public class InboundOrderDtoInsert {

      
     private Long supplierId;   // Proveedor
    private String status;     // Estado de la orden
    private List<InboundOrderItemDtoInsert> items = new ArrayList<>();
     private Long warehouseId;


    public InboundOrderDtoInsert(Long supplierId,Long warehouseId, String status, List<InboundOrderItemDtoInsert> items) {
      this.supplierId = supplierId;
      this.status = status;
      this.items = items;
      this.warehouseId = warehouseId;
    }


    public InboundOrderDtoInsert() {
      
    }


    public Long getSupplierId() {
      return supplierId;
    }


    public void setSupplierId(Long supplierId) {
      this.supplierId = supplierId;
    }


    public String getStatus() {
      return status;
    }


    public void setStatus(String status) {
      this.status = status;
    }


    public List<InboundOrderItemDtoInsert> getItems() {
      return items;
    }


    public void setItems(List<InboundOrderItemDtoInsert> items) {
      this.items = items;
    }


    public Long getWarehouseId() {
        return warehouseId;
    }


    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    

    

    




    



    



   
    

    








}
