package com.springweb.inboundOrder.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springweb.inboundOrder.domain.InboundOrder;
import com.springweb.inboundOrder.dto.InboundOrderCancelRequest;
import com.springweb.inboundOrder.dto.InboundOrderCancelResponseDto;
import com.springweb.inboundOrder.dto.InboundOrderDtoInsert;
import com.springweb.inboundOrder.dto.InboundOrderDtoReturn;
import com.springweb.inboundOrder.dto.InboundOrderPendingSelectDto;
import com.springweb.inboundOrder.dto.InboundOrderPreviewDto;
import com.springweb.inboundOrder.dto.InboundOrderReceiveDto;
import com.springweb.inboundOrder.dto.InboundReceiptResponseDto;
import com.springweb.inboundOrder.service.InboundOrderServiceImpl;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/inbound-orders")
@CrossOrigin(origins = "*")
public class InboundOrderController {

private final InboundOrderServiceImpl inboundOrderServiceImpl;

public InboundOrderController(InboundOrderServiceImpl inboundOrderServiceImpl) {
  this.inboundOrderServiceImpl = inboundOrderServiceImpl;
}

 @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createInboundOrder(@RequestBody InboundOrderDtoInsert dto) {

      System.out.println("la cantidad de items son ===" + dto.getItems().size());

        InboundOrderDtoReturn inboundOrder = inboundOrderServiceImpl.createInboundOrder(dto);

    // 🔹 Ruta relativa (sin dominio)
    String pdfUrl = "/api/inbound-orders/" + inboundOrder.getId() + "/download";
  

    Map<String, Object> response = new HashMap<>();
    response.put("Numero de Orden", inboundOrder);
    response.put("pdfDownloadUrl", pdfUrl);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


@PostMapping("/{orderNumber}/receive")
public ResponseEntity<InboundReceiptResponseDto> receiveInboundOrder(@PathVariable String orderNumber,@Valid @RequestBody InboundOrderReceiveDto body) {

    InboundReceiptResponseDto dto = inboundOrderServiceImpl.receiveAndAdjust(orderNumber, body);
    return ResponseEntity.ok(dto);
}





      @GetMapping("/{id}/download")
public ResponseEntity<ByteArrayResource> downloadPdf(@PathVariable Long id) throws IOException {
    InboundOrder inboundOrder = inboundOrderServiceImpl.getInboundOrderById(id); // Necesitas este método en el service

    Path pdfPath = Path.of(inboundOrder.getPdfPath()); // Ruta del PDF guardada en la entidad
    byte[] pdfBytes = Files.readAllBytes(pdfPath);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Orden_" + id + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(pdfBytes.length)
            .body(new ByteArrayResource(pdfBytes));

            
}

  @GetMapping("/pending")
    public ResponseEntity<List<InboundOrderPendingSelectDto>> listPending() {
        return ResponseEntity.ok(inboundOrderServiceImpl.listPendingForSelect());
    }

    // InboundOrderController.java
@GetMapping("/{orderNumber}")
public ResponseEntity<Map<String, Object>> getOrderDetail(@PathVariable String orderNumber) {
    Map<String, Object> payload = inboundOrderServiceImpl.getOrderDetailForReceipt(orderNumber);
    return ResponseEntity.ok(payload);
}


  @GetMapping("/{id}/preview")
    public ResponseEntity<InboundOrderPreviewDto> preview(@PathVariable Long id) {
        return ResponseEntity.ok(inboundOrderServiceImpl.getPreview(id));
    }


      @PatchMapping("/{id}/cancel")
    public ResponseEntity<InboundOrderCancelResponseDto> cancel( @PathVariable Long id, @RequestBody(required = false) InboundOrderCancelRequest body,
            @RequestHeader(value = "X-User", required = false) String xUser // opcional si aún no tienes Security
    ) {
        String reason = (body != null) ? body.getReason() : null;
        String actor = (xUser != null && !xUser.isBlank()) ? xUser : "system";
        return ResponseEntity.ok(inboundOrderServiceImpl.cancel(id, reason, actor));
    }
















}










