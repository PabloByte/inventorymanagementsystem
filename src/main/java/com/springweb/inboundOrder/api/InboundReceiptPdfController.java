package com.springweb.inboundOrder.api;

import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springweb.inboundOrder.pdf.FileStorageServiceInboundOrderReceipt;


@RestController
@RequestMapping("/api/inbound-receipts")
public class InboundReceiptPdfController {

            private final FileStorageServiceInboundOrderReceipt pdfService;

    public InboundReceiptPdfController(FileStorageServiceInboundOrderReceipt pdfService) {
        this.pdfService = pdfService;
    }

     /** Ver/descargar el PDF del recibo SIN escribir a disco. */ // debo CREAR LAS RUTAS PARA GUARDAR EN LA BD o en el computador
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPdfInline(@PathVariable Long id, @RequestParam(name = "download", defaultValue = "true") boolean download) {

        byte[] pdf;
        try {
            pdf = pdfService.generatePdf(id);
        } catch (IllegalArgumentException notFound) {
            // Si tu servicio lanza IllegalArgumentException cuando no existe el recibo
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        String filename = "INBOUND_RECEIPT_" + id + ".pdf";
        String disposition = (download ? "attachment" : "inline") + "; filename=\"" + filename + "\"";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.parse(disposition));
        headers.setCacheControl(CacheControl.noStore()); // para demo, evita cache agresivo
        headers.setPragma("no-cache");
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

            }
















        }




