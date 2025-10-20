package com.springweb.catalog.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProductDtoInsert {

  // Identificación
    @NotBlank
    @Size(max = 32)
    private String sku;                 // obligatorio, único

    @Size(max = 64)
    private String barcode;             // opcional (único si viene)

    // Datos principales
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

  

    // Reposición
    @NotNull
    @Min(0)
    private Integer reorderPoint;

    @NotNull
    @Pattern(regexp = "ACTIVE|INACTIVE")
    private String status;              // "ACTIVE" | "INACTIVE"

    // Relaciones
    @NotNull
    private Long categoryId;

    @NotNull
    private Long supplierId;

    /////
    
    private String serial;

    private String lote;

    private String dimensiones; 

    private BigDecimal peso;
    
    private String estadoCertificado;

    private String observacion;



  

    public ProductDtoInsert(@NotBlank @Size(max = 32) String sku, @Size(max = 64) String barcode, @NotBlank String name,
                @NotBlank String description, @NotNull @PositiveOrZero BigDecimal price,
                @NotNull @Min(0) Integer reorderPoint, @NotNull @Pattern(regexp = "ACTIVE|INACTIVE") String status,
                @NotNull Long categoryId, @NotNull Long supplierId, String serial, String lote, String dimensiones,
                BigDecimal peso, String estadoCertificado, String observacion) {
        this.sku = sku;
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.price = price;
        this.reorderPoint = reorderPoint;
        this.status = status;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.serial = serial;
        this.lote = lote;
        this.dimensiones = dimensiones;
        this.peso = peso;
        this.estadoCertificado = estadoCertificado;
        this.observacion = observacion;
}

    public ProductDtoInsert() {
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

    public Long getSupplierId() {
      return supplierId;
    }

    public void setSupplierId(Long supplierId) {
      this.supplierId = supplierId;
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

 

    public String getObservacion() {
      return observacion;
    }

    public void setObservacion(String observacion) {
      this.observacion = observacion;
    }

    public String getEstadoCertificado() {
        return estadoCertificado;
    }

    public void setEstadoCertificado(String estadoCertificado) {
        this.estadoCertificado = estadoCertificado;
    }

    

    

    









}
