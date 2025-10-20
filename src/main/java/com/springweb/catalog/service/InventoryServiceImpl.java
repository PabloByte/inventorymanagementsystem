package com.springweb.catalog.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.springweb.catalog.domain.InventoryLedger;
import com.springweb.catalog.domain.InventoryLedger.MovementType;
import com.springweb.catalog.domain.Stock;
import com.springweb.catalog.repo.InventoryLedgerRepository;
import com.springweb.catalog.repo.ProductRepository;
import com.springweb.catalog.repo.StockRepository;
import com.springweb.catalog.repo.WarehouseRepository;

@Service
public class InventoryServiceImpl implements  IInventoryService {

        private final ProductRepository productRepository;
        private final WarehouseRepository warehouseRepository;
        private final StockRepository stockRepository;
        private final InventoryLedgerRepository inventoryLedgerRepository;

        public InventoryServiceImpl(ProductRepository productRepository, WarehouseRepository warehouseRepository,
                        StockRepository stockRepository, InventoryLedgerRepository inventoryLedgerRepository) {
                this.productRepository = productRepository;
                this.warehouseRepository = warehouseRepository;
                this.stockRepository = stockRepository;
                this.inventoryLedgerRepository = inventoryLedgerRepository;
        }


        @Override
        public InventoryLedger adjustStock(Long productId, Long warehouseId, int qtyDelta, String referenceType, String referenceId, String note, String createdBy) {


                if (qtyDelta == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qtyDelta no puede ser 0");
        }

        var product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe"));

        var warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bodega no existe"));

        // Pequeño bucle de reintento para optimistic locking en contención alta
        int attempts = 0;
        final int maxAttempts = 3;

        while (true) {
            try {
                // 1) Obtener o crear registro Stock(product, warehouse)
                var stock = stockRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                    .orElseGet(() -> {
                        var s = new Stock();
                        s.setProduct(product);
                        s.setWarehouse(warehouse);
                        s.setQuantity(0);
                        return stockRepository.save(s);
                    });

                // 2) Validar no-negativo
                int newQty = stock.getQuantity() + qtyDelta;
                if (newQty < 0) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente en bodega (" + stock.getQuantity() + "), intento de mover " + qtyDelta);
                }

                // 3) Actualizar saldo (optimistic lock por @Version)
                stock.setQuantity(newQty);
                var savedStock = stockRepository.save(stock);

                // 4) Asentar en el kardex (ledger)
                var movementType = resolveMovementType(referenceType, qtyDelta);

                var ledger = new InventoryLedger();
                ledger.setProduct(product);
                ledger.setWarehouse(warehouse);
                ledger.setMovementType(movementType);
                ledger.setQtyDelta(qtyDelta);
                ledger.setBalanceAfter(savedStock.getQuantity());
                ledger.setReferenceType(referenceType);
                ledger.setReferenceId(referenceId);
                ledger.setNote(note);
                ledger.setCreatedBy(createdBy);

                var savedLedger = inventoryLedgerRepository.save(ledger);

                // 5) (Opcional) Refrescar cache en Product.stock con el total real por todas las bodegas
                //    Mientras migras UI, esto mantiene consistencia con vistas antiguas que lean Product.stock
                Integer total = stockRepository.sumQuantityByProductId(productId);
                product.setStock(total != null ? total : 0);
                productRepository.save(product);

                return savedLedger;

            } catch (OptimisticLockingFailureException ex) {
                attempts++;
                if (attempts >= maxAttempts) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Conflicto de concurrencia al ajustar stock. Intente nuevamente.");
                }
                // Reintenta el ciclo leyendo de nuevo y aplicando la operación
            }
        }

        }


         private MovementType resolveMovementType(String referenceType, int qtyDelta) {
        // Normaliza (evita NPEs)
        String ref = referenceType == null ? "" : referenceType.trim().toUpperCase();

        // Reglas:
        // - TRANSFER: OUT si qtyDelta<0 / IN si qtyDelta>0
        // - ADJUSTMENT: siempre ADJUSTMENT (sea +/-)
        // - Caso general: qtyDelta>0 -> INBOUND, qtyDelta<0 -> OUTBOUND
        if ("TRANSFER".equals(ref) || "INTERNAL_TRANSFER".equals(ref)) {
            return qtyDelta > 0 ? MovementType.TRANSFER_IN : MovementType.TRANSFER_OUT;
        }
        if ("ADJUSTMENT".equals(ref) || "AJUSTE".equals(ref)) {
            return MovementType.ADJUSTMENT;
        }
        return qtyDelta > 0 ? MovementType.INBOUND : MovementType.OUTBOUND;
    }











        

}
