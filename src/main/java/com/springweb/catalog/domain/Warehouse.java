package com.springweb.catalog.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(
    name = "warehouses",
    indexes = {
        @Index(name = "idx_warehouses_active", columnList = "active"),
        @Index(name = "idx_warehouses_name", columnList = "name")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_warehouse_code", columnNames = {"code"})
    }
)
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String code; // único

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación 1:N con Stock (una bodega tiene muchos registros de stock)
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    private List<Stock> stockList;

    // Relación 1:N con InventoryLedger (una bodega tiene muchos asientos)
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    private List<InventoryLedger> ledgerEntries;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Warehouse(String code, String name, String address, Boolean active, List<Stock> stockList,
            List<InventoryLedger> ledgerEntries) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.active = active;
        this.stockList = stockList;
        this.ledgerEntries = ledgerEntries;
    }

    public Warehouse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public List<InventoryLedger> getLedgerEntries() {
        return ledgerEntries;
    }

    public void setLedgerEntries(List<InventoryLedger> ledgerEntries) {
        this.ledgerEntries = ledgerEntries;
    }


    






















        

}
