// src/main/java/com/springweb/inboundOrder/dto/InboundOrderDetailItemDto.java
package com.springweb.inboundOrder.dto;

import java.math.BigDecimal;

public class InboundOrderDetailItemDto {
 

       private final Long productId;
    private final String sku;
    private final String name;
    private final Long orderedQty;      // ← Long (SUM)
    private final Long receivedSoFar;   // ← Long (SUM)
    private final java.math.BigDecimal unitCost; // ← BigDecimal


    public InboundOrderDetailItemDto(Long productId, String sku, String name, Long orderedQty, Long receivedSoFar, BigDecimal unitCost) {
        this.productId = productId;
        this.sku = sku;
        this.name = name;
        this.orderedQty = orderedQty;
        this.receivedSoFar = receivedSoFar;
        this.unitCost = unitCost;
    }


    public Long getProductId() {
        return productId;
    }


    public String getSku() {
        return sku;
    }


    public String getName() {
        return name;
    }


    public Long getOrderedQty() {
        return orderedQty;
    }


    public Long getReceivedSoFar() {
        return receivedSoFar;
    }


    public java.math.BigDecimal getUnitCost() {
        return unitCost;
    }

    



    










}
