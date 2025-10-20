package com.springweb.inboundOrder.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.springweb.catalog.domain.Product;
import com.springweb.catalog.domain.Warehouse;
import com.springweb.catalog.repo.ProductRepository;
import com.springweb.catalog.repo.WarehouseRepository;
import com.springweb.catalog.service.InventoryServiceImpl;
import com.springweb.inboundOrder.domain.InboundOrder;
import com.springweb.inboundOrder.domain.InboundOrderItem;
import com.springweb.inboundOrder.domain.InboundReceipt;
import com.springweb.inboundOrder.domain.InboundReceiptItem;
import com.springweb.inboundOrder.dto.InboundOrderCancelResponseDto;
import com.springweb.inboundOrder.dto.InboundOrderDtoInsert;
import com.springweb.inboundOrder.dto.InboundOrderDtoReturn;
import com.springweb.inboundOrder.dto.InboundOrderItemDtoInsert;
import com.springweb.inboundOrder.dto.InboundOrderPendingSelectDto;
import com.springweb.inboundOrder.dto.InboundOrderPreviewDto;
import com.springweb.inboundOrder.dto.InboundOrderReceiveDto;
import com.springweb.inboundOrder.dto.InboundOrderReceiveItemDto;
import com.springweb.inboundOrder.dto.InboundReceiptResponseDto;
import com.springweb.inboundOrder.pdf.FileStorageServiceInboundOrder;
import com.springweb.inboundOrder.repo.InboundOrderRepository;
import com.springweb.inboundOrder.repo.InboundReceiptRepository;
import com.springweb.shared.mapper.InboundReceiptMapper;
import com.springweb.shared.mapper.InventoryManagementMapper;
import com.springweb.suppliers.domain.Supplier;
import com.springweb.suppliers.repo.SupplierRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;



@Service
public class InboundOrderServiceImpl  implements  IInboundOrderService{

  private  final  InboundOrderRepository inboundOrderRepository;
  private final SupplierRepository supplierRepository;
  private final ProductRepository productRepository;
  private final InventoryManagementMapper mapper;
  private final FileStorageServiceInboundOrder fileStorageServiceInboundOrder;
  private final WarehouseRepository warehouseRepository;
  private final InventoryServiceImpl inventoryServiceImpl;
  private final InboundReceiptRepository inboundReceiptRepository;
  private final InboundReceiptMapper inboundReceiptMapper;

    @PersistenceContext
    private EntityManager entityManager;



  public InboundOrderServiceImpl(InboundOrderRepository inboundOrderRepository, SupplierRepository supplierRepository,
                ProductRepository productRepository, InventoryManagementMapper mapper,
                FileStorageServiceInboundOrder fileStorageServiceInboundOrder,
                WarehouseRepository warehouseRepository, InventoryServiceImpl inventoryServiceImpl, InboundReceiptRepository inboundReceiptRepository, InboundReceiptMapper inboundReceiptMapper ) {
        this.inboundOrderRepository = inboundOrderRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.fileStorageServiceInboundOrder = fileStorageServiceInboundOrder;
        this.warehouseRepository = warehouseRepository;
        this.inventoryServiceImpl= inventoryServiceImpl;
        this.inboundReceiptRepository = inboundReceiptRepository;
        this.inboundReceiptMapper = inboundReceiptMapper;
}



  @Override
  public InboundOrder getInboundOrderById (Long id ){

        return inboundOrderRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("No se encontro la orden de compra entrante con el ID ingresado"));

  }

  ZoneId zone = ZoneId.of("America/Bogota");

LocalDate today = LocalDate.now(zone);
LocalDateTime startOfDay = today.atStartOfDay(zone).toLocalDateTime();
LocalDateTime endOfDay = today.plusDays(1).atStartOfDay(zone).toLocalDateTime();





  private String createOrderNumber (){

        // 1. Fecha actual
        LocalDate today = LocalDate.now();
        String month = today.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // Sep
        String day = String.format("%02d", today.getDayOfMonth()); // 02

        // 2. Consecutivo
        long countToday = inboundOrderRepository.countInboundOrdersToday(startOfDay, endOfDay);
        String consecutive = String.format("%03d", countToday); // 001

        // 3. Formato final
        String orderNumber = month + day + consecutive;

        return orderNumber;

  }


  Supplier validateSupplier (Long id) {
   return  supplierRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
  }

  Product validateProduct (Long id ){

   return  productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

  }

   @Override
    public List<InboundOrderPendingSelectDto> listPendingForSelect() {
        return inboundOrderRepository.findPendingForSelect();
    }



  @Override
@Transactional
public InboundOrderDtoReturn createInboundOrder(InboundOrderDtoInsert dto) {
    
  Supplier supplier =   validateSupplier(dto.getSupplierId());

    Warehouse warehouse;
    if (dto.getWarehouseId() != null) {
        warehouse = warehouseRepository.findById(dto.getWarehouseId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));
    } else {
        warehouse = warehouseRepository.findByCode("MAIN")
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "MAIN warehouse not configured"));
    }

    // 2) Crear orden (PENDING por defecto)
    InboundOrder inboundOrder = new InboundOrder();
    inboundOrder.setOrderNumber(createOrderNumber());
    inboundOrder.setSupplier(supplier);
    inboundOrder.setWarehouse(warehouse);
    

    InboundOrder.InboundStatus status = (dto.getStatus() == null || dto.getStatus().isBlank())
        ? InboundOrder.InboundStatus.PENDING
        : InboundOrder.InboundStatus.valueOf(dto.getStatus().toUpperCase());
    inboundOrder.setStatus(status);

    // 3) Items + totalCost
    BigDecimal totalCost = BigDecimal.ZERO;
    for (InboundOrderItemDtoInsert itemDto : dto.getItems()) {

        Product product = validateProduct(itemDto.getProductId());

        InboundOrderItem item = new InboundOrderItem();
        item.setInboundOrder(inboundOrder);
        item.setProduct(product);
        item.setQuantity(itemDto.getQuantity());
        item.setUnitCost(product.getPrice());

        BigDecimal cost = product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
        item.setTotalCost(cost);
        totalCost = totalCost.add(cost);

        inboundOrder.getOrderItems().add(item);
    }
    inboundOrder.setTotalCost(totalCost);

    // 4) Persistir

     Integer totalOrderedInTheInboundOrder = dto.getItems().stream()
    .mapToInt(InboundOrderItemDtoInsert ::getQuantity)
    .sum();

    inboundOrder.setTotalOrdered(totalOrderedInTheInboundOrder);
    inboundOrder.setTotalReceived(0);

    inboundOrderRepository.save(inboundOrder);

    try {
        String pdfPath = fileStorageServiceInboundOrder.saveInboundOrderPdf(inboundOrder);
        inboundOrder.setPdfPath(pdfPath);
    } catch (IOException e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar PDF", e);
    }
    return mapper.inboundOrderToInboundOrderDtoReturn(inboundOrder);
}

InboundOrder validateInboundOrder (String orderNumber ){

return  inboundOrderRepository.findByOrderNumber(orderNumber)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inbound order not found"));

}



@Transactional
@Override
public InboundReceiptResponseDto receiveAndAdjust(String orderNumber, InboundOrderReceiveDto body) {


    InboundOrder order = validateInboundOrder(orderNumber);

    if (order.getStatus() != InboundOrder.InboundStatus.PENDING) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden recibir órdenes en estado PENDING");
    }

    Warehouse wh = order.getWarehouse();
    if (wh == null) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "La orden no tiene warehouse asignado");
    }

    // (1) Cabeza del recibo
    InboundReceipt receipt = new InboundReceipt();
    String receivedBy = (body.getReceivedBy() != null && !body.getReceivedBy().isBlank()) ? body.getReceivedBy() : "system";
    receipt.setReceiptNumber(orderNumber + "receipt");
    receipt.setInboundOrder(order);
    receipt.setWarehouse(wh);
    receipt.setNote(body.getNote());
    receipt.setCreatedBy(receivedBy);
    receipt.setCreatedAt(LocalDateTime.now());

    // (2) Mapas de cantidades pedidas y ya recibidas por producto
    Map<Long, Integer> orderedByProduct = order.getOrderItems().stream()
        .collect(Collectors.groupingBy(it -> it.getProduct().getId(),
                 Collectors.summingInt(InboundOrderItem::getQuantity)));

    Map<Long, Integer> receivedSoFar = (order.getReceipts() == null ? Stream.<InboundReceipt>empty() : order.getReceipts().stream())
        .flatMap(r -> r.getItems().stream())
        .collect(Collectors.groupingBy(i -> i.getProduct().getId(),
                 Collectors.summingInt(InboundReceiptItem::getQuantityReceived)));

    // (3) Armar líneas del recibo
    BigDecimal receiptCost = BigDecimal.ZERO;

    for (InboundOrderReceiveItemDto it : body.getItems()) {
        Product product = productRepository.findById(it.getProductId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + it.getProductId()));

        int receivedQty = it.getReceivedQty();
        if (receivedQty <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad inválida para producto " + product.getName());
        }

        int ordered = orderedByProduct.getOrDefault(product.getId(), 0);
        int already = receivedSoFar.getOrDefault(product.getId(), 0);

        // expectedQty por producto: lo pendiente antes de este recibo
        int expected = Math.max(0, ordered - already);

       

        InboundReceiptItem recItem = new InboundReceiptItem();
        recItem.setProduct(product);
        recItem.setProductSku(product.getSku());
        recItem.setProductName(product.getName());
        recItem.setQuantityReceived(receivedQty);
        recItem.setExpectedQty(expected);

        // Status por renglón
        InboundReceiptItem.LineStatus lineStatus;
        if (receivedQty == 0) {
            lineStatus = InboundReceiptItem.LineStatus.PENDING;
        } else if (expected == 0 && ordered == 0) {
            // producto no pedido: aceptado como OVER (si tu política lo permite)
            lineStatus = InboundReceiptItem.LineStatus.OVER;
        } else if (receivedQty < expected) {
            lineStatus = InboundReceiptItem.LineStatus.PARTIAL;
        } else if (receivedQty == expected) {
            lineStatus = InboundReceiptItem.LineStatus.COMPLETE;
        } else {
            lineStatus = InboundReceiptItem.LineStatus.OVER;
        }
        recItem.setStatus(lineStatus);

        // Costos
        BigDecimal unitCost = (it.getUnitCost() != null) ? it.getUnitCost() : product.getPrice();
        recItem.setUnitCost(unitCost);
        recItem.setTotalCost(unitCost.multiply(BigDecimal.valueOf(receivedQty)));

        // Nota
        recItem.setNote(it.getNote());

        // MUY IMPORTANTE para cascada: agregar a la colección del recibo
        receipt.addItem(recItem);

        // actualizar acumulado local para siguientes renglones del mismo producto
        receivedSoFar.put(product.getId(), already + receivedQty);

        receiptCost = receiptCost.add(recItem.getTotalCost());
    }

    // Totales del recibo (si quieres guardar el costo total en algún lado)
    receipt.setTotalReceived(
        receipt.getItems().stream().mapToInt(InboundReceiptItem::getQuantityReceived).sum()
    );

    // (4) Persistir recibo + ítems por cascada
    inboundReceiptRepository.saveAndFlush(receipt);

    // (5) Ajustar stock y ledger
    for (InboundReceiptItem ri : receipt.getItems()) {
        inventoryServiceImpl.adjustStock(
            ri.getProduct().getId(),
            wh.getId(),
            ri.getQuantityReceived(),
            "INBOUND",
            "REC-" + receipt.getId(),
            "InboundReceipt " + order.getOrderNumber(),
            receivedBy
        );
    }

    // (6) Recalcular totales/estado de la orden
    int totalOrdered = order.getOrderItems().stream()
        .mapToInt(InboundOrderItem::getQuantity).sum();

    // incluir este recibo recién guardado
    int totalReceived = ((order.getReceipts() == null) ? 0 :
        order.getReceipts().stream().flatMap(r -> r.getItems().stream())
            .mapToInt(InboundReceiptItem::getQuantityReceived).sum())
        + receipt.getItems().stream().mapToInt(InboundReceiptItem::getQuantityReceived).sum();

    int pendingLocal = Math.max(0, totalOrdered - totalReceived);

    order.setTotalOrdered(totalOrdered);
    order.setTotalReceived(totalReceived);

    if (pendingLocal == 0) {
        order.setStatus(InboundOrder.InboundStatus.RECEIVED);
        order.setReceivedAt(LocalDateTime.now());
        order.setReceivedBy(receivedBy);
    } else {
        // ⚠️ Alineado con tu enum actual:
        order.setStatus(InboundOrder.InboundStatus.PARTIAL_DELIVERY);
        order.setReceivedAt(LocalDateTime.now());
        order.setReceivedBy(receivedBy);
    }

    inboundOrderRepository.save(order);
    entityManager.flush();
    entityManager.refresh(order);

   InboundReceiptResponseDto resp = inboundReceiptMapper.toDto(receipt);

// ✅ agrega esta línea para que el front tenga el enlace directo:
resp.setPdfUrl("/api/inbound-receipts/" + receipt.getId() + "/pdf");

return resp;




}

// InboundOrderQueryServiceImpl.java
@Override
@Transactional(readOnly = true)
public Map<String, Object> getOrderDetailForReceipt(String orderNumber) {
    
    var header = inboundOrderRepository.findDetailHeaderByOrderNumber(orderNumber);
    if (header == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada: " + orderNumber);
    }

    var items = inboundOrderRepository.findDetailItemsByOrderNumber(orderNumber)
        .stream()
        .map(it -> {
            Map<String, Object> m = new HashMap<>();
            m.put("productId", it.getProductId());
            m.put("sku", it.getSku());
            m.put("name", it.getName());
            m.put("orderedQty", it.getOrderedQty());
            m.put("receivedSoFar", it.getReceivedSoFar());
            m.put("unitCost", it.getUnitCost());
            return m;
        })
        .toList();

    Map<String, Object> resp = new HashMap<>();
    resp.put("orderNumber", header.getOrderNumber());
    resp.put("status", header.getStatus());
    resp.put("supplier", header.getSupplier());
    resp.put("warehouseId", header.getWarehouseId());
    // nombre visible; incluye código si existe
    String warehouseLabel = header.getWarehouse();
    if (header.getWarehouseCode() != null && !header.getWarehouseCode().isBlank()) {
        warehouseLabel = warehouseLabel + " (" + header.getWarehouseCode() + ")";
    }
    resp.put("warehouse", warehouseLabel);
    resp.put("items", items);
    return resp;
}



   @Override
   @Transactional(readOnly = true)
public InboundOrderPreviewDto getPreview(Long id) {

    // TODO Auto-generated method stub
    InboundOrder o = inboundOrderRepository.findByIdWithItemsAndRefs(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "InboundOrder no encontrada"));

        // Supplier
        var supplierDto = InboundOrderPreviewDto.SupplierSummaryDto.builder()
                .name(o.getSupplier() != null ? o.getSupplier().getName() : null)
                .nit(o.getSupplier() != null ? safeNit(o.getSupplier()) : null) // adapta si tu Supplier no tiene NIT
                .build();

        // Warehouse
        var warehouseDto = InboundOrderPreviewDto.WarehouseSummaryDto.builder()
                .name(o.getWarehouse() != null ? o.getWarehouse().getName() : null)
                .code(o.getWarehouse() != null ? o.getWarehouse().getCode() : null)
                .build();

        // Items
        var items = o.getOrderItems() == null ? java.util.List.<InboundOrderItem>of() : o.getOrderItems();
        
        var itemDtos = items.stream().map(it -> {
            var unitPrice = safe(it.getUnitCost());                   // BigDecimal (puede ser null)
            var qty = it.getQuantity() == null ? 0 : it.getQuantity();
            var subtotal = unitPrice.multiply(BigDecimal.valueOf(qty));

            return InboundOrderPreviewDto.ItemPreviewDto.builder()
                    .sku(it.getProduct() != null ? it.getProduct().getSku() : null)
                    .name(it.getProduct() != null ? it.getProduct().getName() : null)
                    .uom(it.getProduct() != null ? safeUom(it.getProduct()) : null)
                    .orderedQty(qty)
                    .unitPrice(unitPrice.compareTo(BigDecimal.ZERO) > 0 ? unitPrice : null) // si no manejas precio por ítem, quedará null
                    .subtotal(unitPrice.compareTo(BigDecimal.ZERO) > 0 ? subtotal : null)
                    .build();
        }).toList();

        // Totales
        int lines = itemDtos.size();
        int units = items.stream()
                .map(i -> i.getQuantity() == null ? 0 : i.getQuantity())
                .reduce(0, Integer::sum);

        // Si manejas totalCost a nivel de orden, lo priorizamos:
        BigDecimal subtotal = safe(o.getTotalCost());
        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            // Si NO tienes totalCost, derivamos de los ítems (solo los que tengan unitPrice)
            subtotal = itemDtos.stream()
                    .map(i -> i.getSubtotal() == null ? BigDecimal.ZERO : i.getSubtotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        BigDecimal tax = BigDecimal.ZERO; // ajusta si manejas impuestos
        BigDecimal grandTotal = subtotal.add(tax);

        var totals = InboundOrderPreviewDto.TotalsPreviewDto.builder()
                .lines(lines)
                .units(units)
                .subtotal(subtotal)
                .tax(tax)
                .grandTotal(grandTotal)
                .build();

        var dtoBuilder = InboundOrderPreviewDto.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .status(o.getStatus() != null ? o.getStatus().name() : null)
                .createdAt(o.getCreatedAt())
                .supplier(supplierDto)
                .warehouse(warehouseDto)
                .items(itemDtos)
                .totals(totals);

        // Si está cancelada, agregamos los campos de traza
        if (o.getStatus() == InboundOrder.InboundStatus.CANCELLED) {
            dtoBuilder
                    .cancelledAt(o.getCancelledAt())
                    .cancelReason(o.getCancelReason())
                    .cancelledBy(o.getCancelledBy());
        }

        return dtoBuilder.build();
    }

    private static BigDecimal safe(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    // Adapta si tu Supplier no tiene NIT/taxId
    private static String safeNit(Object supplier) {
        try {
            var m = supplier.getClass().getMethod("getNit");
            Object v = m.invoke(supplier);
            return Objects.toString(v, null);
        } catch (Exception ignored) {
            return null;
        }
    }

    // Adapta si tu Product no tiene UOM
    private static String safeUom(Object product) {
        try {
            var m = product.getClass().getMethod("getUom");
            Object v = m.invoke(product);
            return Objects.toString(v, null);
        } catch (Exception ignored) {
            return null;
        }
    }



    @Override
    public InboundOrderCancelResponseDto cancel(Long id, String reason, String actor) {

            var order = inboundOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "InboundOrder no encontrada"));

        if (order.getStatus() != InboundOrder.InboundStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se puede cancelar una orden en estado PENDING");
        }

        order.setStatus(InboundOrder.InboundStatus.CANCELLED);
        order.setCancelReason((reason != null && !reason.isBlank()) ? reason.trim() : null);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelledBy((actor != null && !actor.isBlank()) ? actor.trim() : "system");

        var saved = inboundOrderRepository.save(order);

        return InboundOrderCancelResponseDto.builder()
                .id(saved.getId())
                .orderNumber(saved.getOrderNumber())
                .status(saved.getStatus().name())
                .cancelledAt(saved.getCancelledAt())
                .cancelReason(saved.getCancelReason())
                .cancelledBy(saved.getCancelledBy())
                .build();
    }


     










    }







































  


