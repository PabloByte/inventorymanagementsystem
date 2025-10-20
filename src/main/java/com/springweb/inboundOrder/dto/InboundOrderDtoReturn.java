package com.springweb.inboundOrder.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.springweb.inboundOrder.domain.InboundOrder.InboundStatus;

public class InboundOrderDtoReturn {

   private Long id;

    
    private String orderNumber;

   
    private String supplier; // Solo el  nombre

   
    private List<InboundOrderItemDtoReturn> orderItems;

    private BigDecimal totalCost;

   
    private InboundStatus status;
    private String pdfPath;
    private String warehouse; // nombre

     private LocalDateTime receivedAt;
    private String receivedBy;

    private Integer totalOrdered ;

    private Integer totalReceived;




    public InboundOrderDtoReturn(Long id, String orderNumber, String supplier, List<InboundOrderItemDtoReturn> orderItems,
                BigDecimal totalCost, InboundStatus status, String pdfPath, String warehouse, LocalDateTime receivedAt,
                String receivedBy, Integer totalOrdered, Integer totalReceived) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.supplier = supplier;
        this.orderItems = orderItems;
        this.totalCost = totalCost;
        this.status = status;
        this.pdfPath = pdfPath;
        this.warehouse = warehouse;
        this.receivedAt = receivedAt;
        this.receivedBy = receivedBy;
        this.totalOrdered = totalOrdered;
        this.totalReceived = totalReceived;
}


    public InboundOrderDtoReturn() {
    }


    public Long getId() {
      return id;
    }


    public void setId(Long id) {
      this.id = id;
    }


    public String getOrderNumber() {
      return orderNumber;
    }


    public void setOrderNumber(String orderNumber) {
      this.orderNumber = orderNumber;
    }


    public String getSupplier() {
      return supplier;
    }


    public void setSupplier(String supplier) {
      this.supplier = supplier;
    }


    public List<InboundOrderItemDtoReturn> getOrderItems() {
      return orderItems;
    }


    public void setOrderItems(List<InboundOrderItemDtoReturn> orderItems) {
      this.orderItems = orderItems;
    }


    public BigDecimal getTotalCost() {
      return totalCost;
    }


    public void setTotalCost(BigDecimal totalCost) {
      this.totalCost = totalCost;
    }


    public InboundStatus getStatus() {
      return status;
    }


    public void setStatus(InboundStatus status) {
      this.status = status;
    }


    public String getPdfPath() {
      return pdfPath;
    }


    public void setPdfPath(String pdfPath) {
      this.pdfPath = pdfPath;
    }


    public String getWarehouse() {
        return warehouse;
    }


    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }


    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }


    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }


    public String getReceivedBy() {
        return receivedBy;
    }


    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }


    public Integer getTotalOrdered() {
      return totalOrdered;
    }


    public void setTotalOrdered(Integer totalOrdered) {
      this.totalOrdered = totalOrdered;
    }


    public Integer getTotalReceived() {
      return totalReceived;
    }


    public void setTotalReceived(Integer totalReceived) {
      this.totalReceived = totalReceived;
    }

    

    





























}
