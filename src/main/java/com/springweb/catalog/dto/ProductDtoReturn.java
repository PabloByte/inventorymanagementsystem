package com.springweb.catalog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDtoReturn {

    // Identificación
    private Long id;
    private String sku;          // único, clave de negocio
    private String barcode;      // opcional

    // Datos principales
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;

    // Reposición
    private Integer reorderPoint;  // umbral por producto
    private String status;         // "ACTIVE" | "INACTIVE"

    // Relación Category (desnormalizada para evitar LAZY)
    private Long categoryId;
    private String categoryName;

    // Relación Supplier (desnormalizada para evitar LAZY)
    private Long supplierId;
    private String supplierName;

    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
  

    // campos que corresponden a la personalizacion de la empresa 

    private String serial;
    private String lote;
    private String dimensiones;  
      private BigDecimal peso;  
    private String estadoCertificado;  
    private String observacion;




    public ProductDtoReturn(Long id, String sku, String barcode, String name, String description, BigDecimal price,
            Integer stock, Integer reorderPoint, String status, Long categoryId, String categoryName, Long supplierId,
            String supplierName, String serial, String lote, String dimensiones, BigDecimal peso,
            String estadoCertificado, String observacion) {
        this.id = id;
        this.sku = sku;
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.reorderPoint = reorderPoint;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.serial = serial;
        this.lote = lote;
        this.dimensiones = dimensiones;
        this.peso = peso;
        this.estadoCertificado = estadoCertificado;
        this.observacion = observacion;
    }
    

    public ProductDtoReturn() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(Integer reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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


    public String getSerial() {
        return serial;
    }


    public void setSerial(String serial) {
        this.serial = serial;
    }


    public String getLote() {
        return lote;
    }


    public void setLote(String lote) {
        this.lote = lote;
    }


    public String getDimensiones() {
        return dimensiones;
    }


    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }


    public BigDecimal getPeso() {
        return peso;
    }


    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }


    public String getEstadoCertificado() {
        return estadoCertificado;
    }


    public void setEstadoCertificado(String estadoCertificado) {
        this.estadoCertificado = estadoCertificado;
    }


    public String getObservacion() {
        return observacion;
    }


    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }



    

    

 

    

    




}
