package com.springweb.inboundOrder.dto;

import java.math.BigDecimal;

public class InboundOrderItemDtoReturn {


   
    private Long id;


    private String product;

    
    private Integer quantity;

    
    private BigDecimal unitCost;

    
    private BigDecimal totalCost;


    public InboundOrderItemDtoReturn(Long id, String product, Integer quantity, BigDecimal unitCost,
        BigDecimal totalCost) {
      this.id = id;
      this.product = product;
      this.quantity = quantity;
      this.unitCost = unitCost;
      this.totalCost = totalCost;
    }


    public InboundOrderItemDtoReturn() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getProduct() {
        return product;
    }


    public void setProduct(String product) {
        this.product = product;
    }


    public Integer getQuantity() {
        return quantity;
    }


    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public BigDecimal getUnitCost() {
        return unitCost;
    }


    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }


    public BigDecimal getTotalCost() {
        return totalCost;
    }


    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    


















}
