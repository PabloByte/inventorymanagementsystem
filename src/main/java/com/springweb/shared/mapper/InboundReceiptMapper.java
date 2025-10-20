package com.springweb.shared.mapper;





import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.springweb.catalog.domain.Product;
import com.springweb.catalog.domain.Warehouse;
import com.springweb.inboundOrder.domain.InboundOrder;
import com.springweb.inboundOrder.domain.InboundOrderItem;
import com.springweb.inboundOrder.domain.InboundReceipt;
import com.springweb.inboundOrder.domain.InboundReceiptItem;
import com.springweb.inboundOrder.dto.InboundReceiptResponseDto;
import com.springweb.inboundOrder.dto.ReceiptItemDto;
import com.springweb.suppliers.domain.Supplier;

@Mapper(componentModel = "spring")
public interface InboundReceiptMapper {

    // -------- Raíz --------
    @Mapping(target = "receipt", expression = "java(toReceiptInfo(receipt))")
    @Mapping(target = "order",   expression = "java(toOrderInfo(receipt.getInboundOrder()))")
    @Mapping(target = "supplier", expression = "java(toSupplierInfo(receipt.getInboundOrder().getSupplier()))")
    @Mapping(target = "warehouse", expression = "java(toWarehouseInfo(receipt.getWarehouse()))")
    @Mapping(target = "totals", expression = "java(new InboundReceiptResponseDto.Totals())") // lo llenamos en @AfterMapping
    @Mapping(target = "items", source = "items")
    InboundReceiptResponseDto toDto(InboundReceipt receipt);

    // -------- Sub-objetos --------

    default InboundReceiptResponseDto.ReceiptInfo toReceiptInfo(InboundReceipt r) {
        if (r == null) return null;
        InboundReceiptResponseDto.ReceiptInfo dto = new InboundReceiptResponseDto.ReceiptInfo();
        dto.setId(r.getId());
        dto.setNumber(r.getReceiptNumber());
        dto.setNote(r.getNote());
        dto.setCreatedBy(r.getCreatedBy());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

    default InboundReceiptResponseDto.OrderInfo toOrderInfo(InboundOrder o) {
        if (o == null) return null;
        InboundReceiptResponseDto.OrderInfo dto = new InboundReceiptResponseDto.OrderInfo();
        dto.setId(o.getId());
        dto.setNumber(o.getOrderNumber());
        dto.setStatus(o.getStatus() != null ? o.getStatus().name() : null);
        dto.setCreatedAt(o.getCreatedAt());
        dto.setReceivedAt(o.getReceivedAt());
        dto.setReceivedBy(o.getReceivedBy());
        return dto;
    }

    default InboundReceiptResponseDto.SupplierInfo toSupplierInfo(Supplier s) {
        if (s == null) return null;
        InboundReceiptResponseDto.SupplierInfo dto = new InboundReceiptResponseDto.SupplierInfo();
        dto.setId(s.getId());
        dto.setName(s.getName());
        // dto.setTaxId(s.getTaxId()); // si existe en tu entidad
        return dto;
    }

    default InboundReceiptResponseDto.WarehouseInfo toWarehouseInfo(Warehouse w) {
        if (w == null) return null;
        InboundReceiptResponseDto.WarehouseInfo dto = new InboundReceiptResponseDto.WarehouseInfo();
        dto.setId(w.getId());
        dto.setName(w.getName());
        // dto.setCode(w.getCode()); // si existe en tu entidad
        return dto;
    }

    // -------- Ítems --------
    @Mapping(target = "id", source = "id")
    @Mapping(target = "productId", expression = "java(pid(item.getProduct()))")
    @Mapping(target = "productSku", source = "productSku")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "inboundOrderItemId", expression = "java(oid(item.getInboundOrderItem()))")
    @Mapping(target = "expectedQty", source = "expectedQty")
    @Mapping(target = "receivedQty", source = "quantityReceived")
    @Mapping(target = "pendingQty", ignore = true) // se calcula en @AfterMapping
    @Mapping(target = "overQty", ignore = true)    // se calcula en @AfterMapping
    @Mapping(target = "unitCost", source = "unitCost")
    @Mapping(target = "totalCost", source = "totalCost")
    @Mapping(target = "status", expression = "java(item.getStatus() != null ? item.getStatus().name() : null)")
    @Mapping(target = "note", source = "note")
    ReceiptItemDto toItemDto(InboundReceiptItem item);

    // Helpers
    default Long pid(Product p) { return p != null ? p.getId() : null; }
    default Long oid(InboundOrderItem oi) { return oi != null ? oi.getId() : null; }

    // -------- Cálculos derivados después del mapeo --------
    @AfterMapping
    default void computeLineDiffs(InboundReceiptItem src, @MappingTarget ReceiptItemDto tgt) {
        Integer expected = nvl(src.getExpectedQty());
        Integer received = nvl(src.getQuantityReceived());
        int pending = Math.max(0, expected - received);
        int over = Math.max(0, received - expected);
        tgt.setPendingQty(pending);
        tgt.setOverQty(over);
    }

    @AfterMapping
    default void computeTotals(InboundReceipt src, @MappingTarget InboundReceiptResponseDto tgt) {
        InboundReceiptResponseDto.Totals totals = tgt.getTotals();
        if (totals == null) {
            totals = new InboundReceiptResponseDto.Totals();
            tgt.setTotals(totals);
        }

        // Totales del recibo actual
        List<ReceiptItemDto> items = tgt.getItems();
        int lines = items != null ? items.size() : 0;
        int receiptQty = items != null ? items.stream().mapToInt(i -> nvl(i.getReceivedQty())).sum() : 0;
        BigDecimal receiptCost = items != null
                ? items.stream().map(i -> i.getTotalCost() != null ? i.getTotalCost() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        totals.setReceiptTotalLines(lines);
        totals.setReceiptTotalQty(receiptQty);
        totals.setReceiptTotalCost(receiptCost);

        // Totales de la orden (incluye este recibo)
        InboundOrder order = src.getInboundOrder();
        int orderedQty = 0;
        if (order != null && order.getOrderItems() != null) {
            orderedQty = order.getOrderItems().stream()
                    .mapToInt(InboundOrderItem::getQuantity)
                    .sum();
        }

        // ya recibidos (previos + este recibo)
        int receivedQty = 0;
        if (order != null && order.getReceipts() != null) {
            receivedQty = order.getReceipts().stream()
                    .flatMap(r -> r.getItems().stream())
                    .mapToInt(InboundReceiptItem::getQuantityReceived)
                    .sum();
        }
        // sumar los de este recibo si no están aún en la colección de la orden
        if (src.getItems() != null && (order == null || order.getReceipts() == null || !order.getReceipts().contains(src))) {
            receivedQty += src.getItems().stream().mapToInt(InboundReceiptItem::getQuantityReceived).sum();
        }

        int pendingQty = Math.max(0, orderedQty - receivedQty);

        totals.setOrderTotalOrderedQty(orderedQty);
        totals.setOrderTotalReceivedQty(receivedQty);
        totals.setOrderTotalPendingQty(pendingQty);
    }

    // util
    private static Integer nvl(Integer v) { return v != null ? v : 0; }
}
