package com.springweb.inboundOrder.domain;



import java.math.BigDecimal;

import com.springweb.catalog.domain.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "inbound_receipt_items",
    indexes = {
        @Index(name = "idx_inb_receipt_items_receipt", columnList = "inbound_receipt_id"),
        @Index(name = "idx_inb_receipt_items_product", columnList = "product_id")
    },
    uniqueConstraints = {
        // un producto una sola fila por recibo (si luego quieres permitir varias, elimina esta constraint)
        @UniqueConstraint(name = "uq_inb_receipt_items_prod_per_receipt", columnNames = {"inbound_receipt_id","product_id"})
    }
)

@NoArgsConstructor
@AllArgsConstructor
public class InboundReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // vínculo al recibo
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inbound_receipt_id", nullable = false)
    private InboundReceipt inboundReceipt;

    // referencia al producto (para ajustar stock y ledger)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // snapshot del producto (para que el PDF/recibo quede estable en el tiempo)
    @Column(name = "product_sku", nullable = false, length = 32)
    private String productSku;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    // (opcional) vínculo al renglón de la orden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_order_item_id")
    private InboundOrderItem inboundOrderItem;

    @Column(name = "expected_qty", nullable = false)
    private Integer expectedQty = 0;          // lo pedido

    @Column(name = "quantity_received", nullable = false)
    private Integer quantityReceived = 0;     // lo recibido editable

    @Column(name = "unit_cost", precision = 19, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "total_cost", precision = 19, scale = 2)
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private LineStatus status = LineStatus.PENDING; // PENDING|PARTIAL|COMPLETE|OVER|CANCELLED

    @Column(name = "note")
    private String note;

    public enum LineStatus {
        PENDING, PARTIAL, COMPLETE, OVER, CANCELLED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InboundReceipt getInboundReceipt() {
        return inboundReceipt;
    }

    public void setInboundReceipt(InboundReceipt inboundReceipt) {
        this.inboundReceipt = inboundReceipt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public InboundOrderItem getInboundOrderItem() {
        return inboundOrderItem;
    }

    public void setInboundOrderItem(InboundOrderItem inboundOrderItem) {
        this.inboundOrderItem = inboundOrderItem;
    }

    public Integer getExpectedQty() {
        return expectedQty;
    }

    public void setExpectedQty(Integer expectedQty) {
        this.expectedQty = expectedQty;
    }

    public Integer getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
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

    public LineStatus getStatus() {
        return status;
    }

    public void setStatus(LineStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    

    


}


