package com.springweb.inboundOrder.dto;



import java.math.BigDecimal;
import lombok.Data;

@Data
public class ReceiptItemDto {

    private Long id;

    private Long productId;
    private String productSku;
    private String productName;

    private Long inboundOrderItemId;   // si existe vínculo al renglón

    private Integer expectedQty;       // lo pendiente antes de este recibo (por producto o por renglón, según tu regla)
    private Integer receivedQty;       // quantityReceived del item
    private Integer pendingQty;        // max(0, expectedQty - receivedQty)
    private Integer overQty;           // max(0, receivedQty - expectedQty)

    private BigDecimal unitCost;
    private BigDecimal totalCost;

    private String status;             // LineStatus como String (PENDING|PARTIAL|COMPLETE|OVER|CANCELLED)
    private String note;
}

