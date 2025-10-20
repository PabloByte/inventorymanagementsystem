package com.springweb.catalog.service;

import com.springweb.catalog.domain.InventoryLedger;

public interface IInventoryService {

   InventoryLedger adjustStock(
        Long productId,
        Long warehouseId,
        int qtyDelta,
        String referenceType,  // ej. "INBOUND_ORDER", "OUTBOUND_ORDER", "ADJUSTMENT", "TRANSFER"
        String referenceId,    // id/uuid del documento origen
        String note,
        String createdBy
    );









}
