package com.springweb.catalog.domain;

import java.time.LocalDateTime;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "inventory_ledger",
    indexes = {
        @Index(name = "idx_ledger_prod_wh_time", columnList = "product_id, warehouse_id, created_at"),
        @Index(name = "idx_ledger_prod_time", columnList = "product_id, created_at"),
        @Index(name = "idx_ledger_ref", columnList = "reference_type, reference_id")
    }
)
public class InventoryLedger {


  public enum MovementType {
        INBOUND,        // ingreso (recepción)
        OUTBOUND,       // salida (despacho)
        ADJUSTMENT,     // ajuste manual (+/-)
        TRANSFER_OUT,   // transferencia: sale de bodega origen
        TRANSFER_IN,    // transferencia: entra a bodega destino
        RESERVE,        // reserva (si implementas reservado)
        RELEASE         // liberar reserva
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1 con Product
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // N:1 con Warehouse
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 32)
    private MovementType movementType;

    @Column(name = "qty_delta", nullable = false)
    private Integer qtyDelta; // positivo suma, negativo resta (≠ 0)

    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter; // saldo resultante en ESA bodega (>= 0)

    @Column(name = "reference_type", length = 64)
    private String referenceType; // p.ej. INBOUND_ORDER, OUTBOUND_ORDER, ADJUSTMENT...

    @Column(name = "reference_id", length = 64)
    private String referenceId;   // id/uuid del documento origen

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public InventoryLedger( Product product, Warehouse warehouse, MovementType movementType, Integer qtyDelta,
                Integer balanceAfter, String referenceType, String referenceId, String note, String createdBy,
                LocalDateTime createdAt) {
       
        this.product = product;
        this.warehouse = warehouse;
        this.movementType = movementType;
        this.qtyDelta = qtyDelta;
        this.balanceAfter = balanceAfter;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.note = note;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public InventoryLedger() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Integer getQtyDelta() {
        return qtyDelta;
    }

    public void setQtyDelta(Integer qtyDelta) {
        this.qtyDelta = qtyDelta;
    }

    public Integer getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Integer balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
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

    

    
    

















}
