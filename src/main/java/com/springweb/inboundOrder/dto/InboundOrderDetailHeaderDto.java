package com.springweb.inboundOrder.dto;



public class InboundOrderDetailHeaderDto {
    private final String orderNumber;
    private final String status;
    private final String supplier;
    private final Long warehouseId;
    private final String warehouse;   // nombre
    private final String warehouseCode;

    public InboundOrderDetailHeaderDto(
            String orderNumber,
            String status,
            String supplier,
            Long warehouseId,
            String warehouse,
            String warehouseCode
    ) {
        this.orderNumber = orderNumber;
        this.status = status;
        this.supplier = supplier;
        this.warehouseId = warehouseId;
        this.warehouse = warehouse;
        this.warehouseCode = warehouseCode;
    }

    public String getOrderNumber() { return orderNumber; }
    public String getStatus()      { return status; }
    public String getSupplier()    { return supplier; }
    public Long getWarehouseId()   { return warehouseId; }
    public String getWarehouse()   { return warehouse; }
    public String getWarehouseCode(){ return warehouseCode; }
}

