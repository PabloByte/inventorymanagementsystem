package com.springweb.inboundOrder.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.springweb.catalog.domain.Warehouse;
import com.springweb.suppliers.domain.Supplier;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "inbound_orders",
       indexes = {
         @Index(name = "idx_inbound_order_number", columnList = "order_number"),
         @Index(name = "idx_inbound_warehouse", columnList = "warehouse_id"),
         @Index(name = "idx_inbound_status", columnList = "status")
       })
public class InboundOrder {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier; // Proveedor que suministra productos


      // 🔹 NUEVO: bodega donde se recibirá esta orden
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;


    @OneToMany(mappedBy = "inboundOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InboundOrderItem> orderItems;

    @OneToMany(mappedBy = "inboundOrder", fetch = FetchType.LAZY)
private List<InboundReceipt> receipts = new ArrayList<>();

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InboundStatus status;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

     @Column(name = "received_by", length = 100)
    private String receivedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String pdfPath;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum InboundStatus {
        PENDING, RECEIVED, CANCELLED, PARTIAL_DELIVERY
    }

     @NotNull 
     private Integer totalOrdered ;

    private Integer totalReceived;

    @Column(name="total_pending", insertable= false, updatable= false)
    private Integer totalPending;

    // --- Auditoría de cancelación ---
@Column(name = "cancel_reason", length = 255)
private String cancelReason;

@Column(name = "cancelled_at")
private LocalDateTime cancelledAt;

@Column(name = "cancelled_by", length = 100)
private String cancelledBy;







  

    public InboundOrder(Long id, String orderNumber, Supplier supplier, Warehouse warehouse,
                List<InboundOrderItem> orderItems, List<InboundReceipt> receipts, BigDecimal totalCost,
                InboundStatus status, LocalDateTime receivedAt, String receivedBy, LocalDateTime createdAt,
                LocalDateTime updatedAt, String pdfPath, Integer totalOrdered, Integer totalReceived, Integer totalPending) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.supplier = supplier;
        this.warehouse = warehouse;
        this.orderItems = orderItems;
        this.receipts = receipts;
        this.totalCost = totalCost;
        this.status = status;
        this.receivedAt = receivedAt;
        this.receivedBy = receivedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pdfPath = pdfPath;
        this.totalOrdered = totalOrdered;
        this.totalReceived = totalReceived;
        this.totalPending = totalPending;
}

    public InboundOrder() {
      orderItems = new ArrayList<>();
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

    public Supplier getSupplier() {
      return supplier;
    }

    public void setSupplier(Supplier supplier) {
      this.supplier = supplier;
    }

    public List<InboundOrderItem> getOrderItems() {
      return orderItems;
    }

    public void setOrderItems(List<InboundOrderItem> orderItems) {
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

    public LocalDateTime getCreatedAt() {
      return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
      return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
    }

    public String getPdfPath() {
      return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
      this.pdfPath = pdfPath;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
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

    public List<InboundReceipt> getReceipts() {
      return receipts;
    }

    public void setReceipts(List<InboundReceipt> receipts) {
      this.receipts = receipts;
    }

    public Integer getTotalOrdered() {
      return totalOrdered;
    }

    public void setTotalOrdered(int totalOrdered) {
      this.totalOrdered = totalOrdered;
    }

    public Integer getTotalReceived() {
      return totalReceived;
    }

    public void setTotalReceived(int totalReceived) {
      this.totalReceived = totalReceived;
    }

    public Integer getTotalPending() {
      return totalPending;
    }

    public void setTotalPending(int totalPending) {
      this.totalPending = totalPending;
    }

    public String getCancelReason() {
      return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
      this.cancelReason = cancelReason;
    }

    public LocalDateTime getCancelledAt() {
      return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
      this.cancelledAt = cancelledAt;
    }

    public String getCancelledBy() {
      return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
      this.cancelledBy = cancelledBy;
    }

    

    

    

    























  

}
