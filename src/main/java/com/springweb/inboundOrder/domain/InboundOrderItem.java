package com.springweb.inboundOrder.domain;

import java.math.BigDecimal;

import com.springweb.catalog.domain.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inbound_order_items")
public class InboundOrderItem {



      @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_order_id", nullable = false)
    private InboundOrder inboundOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost", nullable = false)
    private BigDecimal unitCost;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    public InboundOrderItem(Long id, InboundOrder inboundOrder, Product product, Integer quantity, BigDecimal unitCost,
        BigDecimal totalCost) {
      this.id = id;
      this.inboundOrder = inboundOrder;
      this.product = product;
      this.quantity = quantity;
      this.unitCost = unitCost;
      this.totalCost = totalCost;
    }

    public InboundOrderItem() {
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public InboundOrder getInboundOrder() {
      return inboundOrder;
    }

    public void setInboundOrder(InboundOrder inboundOrder) {
      this.inboundOrder = inboundOrder;
    }

    public Product getProduct() {
      return product;
    }

    public void setProduct(Product product) {
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
