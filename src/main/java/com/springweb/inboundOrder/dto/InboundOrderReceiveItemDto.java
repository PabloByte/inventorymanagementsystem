package com.springweb.inboundOrder.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InboundOrderReceiveItemDto {

   @NotNull      
 private Long productId;
 
  @Min(1)      
 @NotNull           
private Integer receivedQty;

private String note;

private BigDecimal unitCost; // opcional, si quieres registrar costo real del recibo



public InboundOrderReceiveItemDto() {
}

    public InboundOrderReceiveItemDto(String note, Long productId, Integer receivedQty, BigDecimal unitCost) {
        this.note = "Nada que comentar por defecto";
        this.productId = productId;
        this.receivedQty = receivedQty;
        this.unitCost = unitCost;
    }

public Long getProductId() {
        return productId;
}

public void setProductId(Long productId) {
        this.productId = productId;
}

public Integer getReceivedQty() {
        return receivedQty;
}

public void setReceivedQty(Integer receivedQty) {
        this.receivedQty = receivedQty;
}

public BigDecimal getUnitCost() {
        return unitCost;
}

public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
}

public String getNote() {
        return note;
}

public void setNote(String note) {
        this.note = note;
}

















}
