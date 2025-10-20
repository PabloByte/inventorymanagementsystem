package com.springweb.inboundOrder.dto;

public class InboundOrderItemDtoInsert {


 private Long productId;
    private Integer quantity;


    public InboundOrderItemDtoInsert(Long productId, Integer quantity ) {
      this.productId = productId;
      this.quantity = quantity;
      
    }


    public InboundOrderItemDtoInsert() {
    }


    public Long getProductId() {
      return productId;
    }


    public void setProductId(Long productId) {
      this.productId = productId;
    }


    public Integer getQuantity() {
      return quantity;
    }


    public void setQuantity(Integer quantity) {
      this.quantity = quantity;
    }



    

    



}
