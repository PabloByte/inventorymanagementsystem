package com.springweb.catalog.dto.view;

public interface ReorderProductView {

  Long getId();
    String getSku();
    String getBarcode();
    String getName();
    String getDescription();
    java.math.BigDecimal getPrice();
    Integer getStock();
    Integer getReorderPoint();
    String getStatus();

    Long getCategoryId();
    String getCategoryName();
    Long getSupplierId();
    String getSupplierName();

    java.time.LocalDateTime getCreatedAt();
    java.time.LocalDateTime getUpdatedAt();


}
