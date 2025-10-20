package com.springweb.inboundOrder.domain;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.springweb.catalog.domain.Warehouse;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "inbound_receipts",
    indexes = {
        @Index(name = "idx_inb_receipt_order", columnList = "inbound_order_id"),
        @Index(name = "idx_inb_receipt_wh", columnList = "warehouse_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_inb_receipt_number", columnNames = {"receipt_number"})
    }
)

@NoArgsConstructor
@AllArgsConstructor
public class InboundReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receipt_number", nullable = false, length = 50)
    private String receiptNumber; // consecutivo legible

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inbound_order_id", nullable = false)
    private InboundOrder inboundOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "inboundReceipt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InboundReceiptItem> items = new ArrayList<>();

    @Column(name = "total_received", nullable = false)
    private Integer totalReceived = 0;

    @Column(name = "note")
    private String note;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public InboundOrder getInboundOrder() {
        return inboundOrder;
    }

    public void setInboundOrder(InboundOrder inboundOrder) {
        this.inboundOrder = inboundOrder;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public List<InboundReceiptItem> getItems() {
        return items;
    }

    public void setItems(List<InboundReceiptItem> items) {
        this.items = items;
    }

    public Integer getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(Integer totalReceived) {
        this.totalReceived = totalReceived;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public void addItem(InboundReceiptItem item) {
    if (item == null) return;
    item.setInboundReceipt(this);   // back-ref
    this.items.add(item);           // dueño de la cascada
}

public void removeItem(InboundReceiptItem item) {
    if (item == null) return;
    this.items.remove(item);
    item.setInboundReceipt(null);
}

    
}

