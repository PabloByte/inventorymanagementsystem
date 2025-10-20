package com.springweb.inboundOrder.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InboundOrderPreviewDto {

            private Long id;
    private String orderNumber;
    private String status;            // "PENDING" | "CANCELLED" | ...
    private LocalDateTime createdAt;

    private SupplierSummaryDto supplier;
    private WarehouseSummaryDto warehouse;

    private List<ItemPreviewDto> items;
    private TotalsPreviewDto totals;

    // Solo se rellenan si la orden está cancelada
    private LocalDateTime cancelledAt;
    private String cancelReason;
    private String cancelledBy;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SupplierSummaryDto {
        private String name;
        private String nit; // opcional si lo tienes en Supplier
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class WarehouseSummaryDto {
        private String name;
        private String code;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ItemPreviewDto {
        private String sku;
        private String name;
        private String uom;                 // opcional si existe en Product
        private Integer orderedQty;
        private BigDecimal unitPrice;       // opcional si manejas precio por ítem
        private BigDecimal subtotal;        // orderedQty * unitPrice (si aplica)
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TotalsPreviewDto {
        private Integer lines;
        private Integer units;
        private BigDecimal subtotal;        // suma de subtotales o totalCost de la orden
        private BigDecimal tax;             // si no manejas impuestos: BigDecimal.ZERO
        private BigDecimal grandTotal;      // subtotal + tax
    }



















}
