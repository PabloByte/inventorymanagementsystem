package com.springweb.inboundOrder.dto;

public class InboundOrderPendingSelectDto {

        
    private final String orderNumber;
    private final Long id;
    private final String supplierName;
    private final String warehouseName;
    private final String warehouseCode;

    public InboundOrderPendingSelectDto(
            String orderNumber,
            Long id,
            String supplierName,
            String warehouseName,
            String warehouseCode
    ) {
        this.orderNumber = orderNumber;
        this.id = id;
        this.supplierName = supplierName;
        this.warehouseName = warehouseName;
        this.warehouseCode = warehouseCode;
    }

    public String getOrderNumber() { return orderNumber; }
    public Long getId() { return id; }
    public String getSupplierName() { return supplierName; }
    public String getWarehouseName() { return warehouseName; }
    public String getWarehouseCode() { return warehouseCode; }
}
