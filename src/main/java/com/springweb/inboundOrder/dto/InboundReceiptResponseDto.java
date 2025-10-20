package com.springweb.inboundOrder.dto;

// src/main/java/com/springweb/inboundReceipt/dto/InboundReceiptResponseDto.java


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class InboundReceiptResponseDto {

    private ReceiptInfo receipt;      // info del recibo
    private OrderInfo order;          // info resumida de la orden
    private SupplierInfo supplier;    // proveedor
    private WarehouseInfo warehouse;  // bodega

    private Totals totals;            // totales útiles para UI
    private List<ReceiptItemDto> items;

    @Data
    public static class ReceiptInfo {
        private Long id;
        private String number;        // receiptNumber
        private String note;
        private String createdBy;
        private LocalDateTime createdAt;
    }

    @Data
    public static class OrderInfo {
        private Long id;
        private String number;        // orderNumber
        private String status;        // enum como String (alineado a tu CHECK)
        private LocalDateTime createdAt;
        private LocalDateTime receivedAt;
        private String receivedBy;
    }

    @Data
    public static class SupplierInfo {
        private Long id;
        private String name;
        private String taxId;    
        private String pdfUrl;     // si lo tienes; si no, omite en mapper
    }

    @Data
    public static class WarehouseInfo {
        private Long id;
        private String code;          // si tienes código/código corto
        private String name;
    }

    @Data
    public static class Totals {
        // del recibo actual
        private Integer receiptTotalLines;         // # renglones
        private Integer receiptTotalQty;           // sum(quantityReceived)
        private BigDecimal receiptTotalCost;       // sum(totalCost)

        // de la orden (acumulados)
        private Integer orderTotalOrderedQty;      // sum(orderItems.quantity)
        private Integer orderTotalReceivedQty;     // sum(receiptItems.quantityReceived) (incluye este recibo)
        private Integer orderTotalPendingQty;      // ordered - received (>= 0)
    }

    private String pdfUrl;





    
}



