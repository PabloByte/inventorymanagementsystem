package com.springweb.inboundOrder.pdf;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import com.springweb.catalog.domain.Product;
import com.springweb.inboundOrder.domain.InboundOrder;
import com.springweb.inboundOrder.domain.InboundOrderItem;

@Service
public class FileStorageServiceInboundOrder {

  private static  final String PDF_STORAGE_DIR = "C:/Users/PC/Documents/PDFGenerateAutomatically/";

public String saveInboundOrderPdf(InboundOrder inboundOrder) throws IOException {

    // === Preparación carpeta/archivo ===
    File dir = new File(PDF_STORAGE_DIR);
    if (!dir.exists()) dir.mkdirs();

    String fileName = "OrdenEntrada_" + inboundOrder.getOrderNumber() + ".pdf";
    String filePath = PDF_STORAGE_DIR + fileName;

    // === Utilidades de formato ===
    NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // === Colores ===
    final Color LIGHT_BLUE  = new Color(173, 216, 230);
    final Color HEADER_BLUE = new Color(52, 152, 219);
    final Color TEXT_DARK   = Color.BLACK;
    final Color ROW_ALT     = new Color(245, 247, 250);
    final Color TEXT_MUTED  = new Color(90, 99, 110); // gris para detalle

    // === Márgenes y geometría ===
    float margin     = 40f;
    float pageWidth  = PDRectangle.A4.getWidth();
    float pageHeight = PDRectangle.A4.getHeight();

    float tableLeft  = margin;
    float tableRight = pageWidth - margin;
    float tableWidth = tableRight - tableLeft;

    float y = pageHeight - margin; // cursor vertical

    try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {

            // ==== TÍTULO ====
            content.setNonStrokingColor(HEADER_BLUE);
            content.addRect(tableLeft, y - 28, tableWidth, 28);
            content.fill();

            content.beginText();
            content.setNonStrokingColor(Color.WHITE);
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.newLineAtOffset(tableLeft + 12, y - 20);
            content.showText("Orden de Entrada de Almacén");
            content.endText();
            y -= 40;

            // ==== CONTORNO GENERAL ====
            content.setStrokingColor(LIGHT_BLUE);
            content.setLineWidth(1.2f);
            content.addRect(margin, margin, pageWidth - 2*margin, pageHeight - 2*margin);
            content.stroke();

            // ==== DATOS GENERALES (2 columnas) ====
            float colGap = 220f;

            content.setNonStrokingColor(TEXT_DARK);
            content.setFont(PDType1Font.HELVETICA, 11);

            float xLeft = tableLeft + 12;
            float yInfo = y;

            content.beginText();
            content.newLineAtOffset(xLeft, yInfo);
            content.showText("ID: " + inboundOrder.getId());
            content.newLineAtOffset(0, -16);
            content.showText("Número de Orden: " + safe(inboundOrder.getOrderNumber()));
            content.newLineAtOffset(0, -16);
            content.showText("Proveedor: " + safe(inboundOrder.getSupplier() != null ? inboundOrder.getSupplier().getName() : "—"));
            content.newLineAtOffset(0, -16);
            content.showText("Estado: " + safe(String.valueOf(inboundOrder.getStatus())));
            content.endText();

            float xRight = xLeft + colGap;
            String whCode = (inboundOrder.getWarehouse() != null ? safe(inboundOrder.getWarehouse().getCode()) : "");
            String whName = (inboundOrder.getWarehouse() != null ? safe(inboundOrder.getWarehouse().getName()) : "");
            Long whId     = (inboundOrder.getWarehouse() != null ? inboundOrder.getWarehouse().getId() : null);

            content.beginText();
            content.newLineAtOffset(xRight, yInfo);
            content.showText("Bodega: " +
                    (whId != null ? "#" + whId + " - " : "") +
                    (whName.isEmpty() ? "—" : whName) +
                    (whCode.isEmpty() ? "" : " (" + whCode + ")"));
            content.newLineAtOffset(0, -16);
            content.showText("Fecha de Creación: " + (
                    inboundOrder.getCreatedAt() != null ? inboundOrder.getCreatedAt().format(dtf) : "—"
            ));
            content.endText();

            y -= 90;

            // ==== ENCABEZADO DE TABLA ====
            float baseRowHeight = 22f; // base; aumentará si hay detalle
            float[] colWidths = { tableWidth * 0.48f, tableWidth * 0.15f, tableWidth * 0.18f, tableWidth * 0.19f };
            float[] colX = new float[colWidths.length + 1];
            colX[0] = tableLeft;
            for (int i = 0; i < colWidths.length; i++) colX[i + 1] = colX[i] + colWidths[i];

            // fondo encabezado
            content.setNonStrokingColor(new Color(235, 245, 255));
            content.addRect(tableLeft, y - baseRowHeight, tableWidth, baseRowHeight);
            content.fill();

            // borde encabezado
            content.setStrokingColor(LIGHT_BLUE);
            content.setLineWidth(1f);
            content.addRect(tableLeft, y - baseRowHeight, tableWidth, baseRowHeight);
            content.stroke();

            // títulos
            String[] headers = {"Código / Producto", "Cantidad", "Costo Unitario", "Total"};
            content.setNonStrokingColor(TEXT_DARK);
            content.setFont(PDType1Font.HELVETICA_BOLD, 11);

            float textYOffset = y - 16;
            for (int i = 0; i < headers.length; i++) {
                content.beginText();
                content.newLineAtOffset(colX[i] + 10f, textYOffset);
                content.showText(headers[i]);
                content.endText();
            }

            y -= baseRowHeight;

            // ==== FILAS ====
            content.setFont(PDType1Font.HELVETICA, 10.5f);

            BigDecimal grandTotal = BigDecimal.ZERO;
            int rowIndex = 0;

            if (inboundOrder.getOrderItems() != null) {
                for (InboundOrderItem item : inboundOrder.getOrderItems()) {

                    // ===== Datos =====
                    String prodName = safe(item.getProduct() != null ? item.getProduct().getName() : "—");
                    String sku      = safe(item.getProduct() != null ? item.getProduct().getSku()  : "—");
                    String qtyStr   = String.valueOf(item.getQuantity());
                    BigDecimal unit  = (item.getUnitCost() != null ? item.getUnitCost() : BigDecimal.ZERO);
                    String unitCostStr = money.format(unit);

                    BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 0));
                    grandTotal = grandTotal.add(lineTotal);
                    String lineTotalStr = money.format(lineTotal);

                    // ===== Detalle de producto (envuelto en múltiples líneas si hace falta) =====
Product p = item.getProduct();
String serial  = (p != null && p.getSerial() != null && !p.getSerial().isBlank()) ? "Serie: " + safe(p.getSerial()) : null;
String lote    = (p != null && p.getLote()   != null && !p.getLote().isBlank())   ? "Lote: "  + safe(p.getLote())   : null;
String dim     = (p != null && p.getDimensiones() != null && !p.getDimensiones().isBlank()) ? "Dim: " + safe(p.getDimensiones()) : null;
String peso    = (p != null && p.getPeso() != null) ? ("Peso: " + p.getPeso().stripTrailingZeros().toPlainString() + " kg") : null;
String cert    = (p != null && p.getEstadoCertificado() != null) ? ("Cert: " + p.getEstadoCertificado().name()) : null;
String obs     = (p != null && p.getObservacion() != null && !p.getObservacion().isBlank()) ? ("Obs: " + safe(p.getObservacion())) : null;

// Une solo lo presente
String detail = java.util.stream.Stream.of(serial, lote, dim, peso, cert, obs)
        .filter(java.util.Objects::nonNull)
        .collect(java.util.stream.Collectors.joining(" | "));

// Parámetros de detalle envuelto
final PDFont DETAIL_FONT = PDType1Font.HELVETICA;
final float  DETAIL_SIZE = 9.0f;
final float  DETAIL_LEADING = 11f; // separación entre líneas del detalle
float maxDetailWidth = colWidths[0] - 20f; // padding izq/der 10px

java.util.List<String> detailLines = detail.isEmpty()
        ? java.util.Collections.emptyList()
        : wrapText(detail, DETAIL_FONT, DETAIL_SIZE, maxDetailWidth);

// Altura dinámica de la fila: base + líneas de detalle
float rowHeight = baseRowHeight + (detailLines.isEmpty() ? 0f : DETAIL_LEADING * detailLines.size());

// Alternar fondo
if (rowIndex % 2 == 1) {
    content.setNonStrokingColor(ROW_ALT);
    content.addRect(tableLeft, y - rowHeight, tableWidth, rowHeight);
    content.fill();
}

// Texto combinado en primera columna: "SKU • Nombre"
String codeAndName = (sku.equals("—") ? "" : sku) + (sku.equals("—") ? "" : " • ") + prodName;

// ===== Render principal de la fila =====
float baseY = y - 15;

// 1) Código / Producto (línea principal)
content.setNonStrokingColor(TEXT_DARK);
content.setFont(PDType1Font.HELVETICA, 10.5f);
content.beginText();
content.newLineAtOffset(colX[0] + 10f, baseY);
content.showText(trimToFit(codeAndName, (int) colWidths[0], PDType1Font.HELVETICA, 10.5f));
content.endText();

// 1b) Detalle envuelto (múltiples líneas)
if (!detailLines.isEmpty()) {
    content.setNonStrokingColor(new Color(90, 99, 110)); // gris
    content.setFont(DETAIL_FONT, DETAIL_SIZE);
    float dy = 11f; // desplazamiento desde la primera línea
    for (String line : detailLines) {
        content.beginText();
        content.newLineAtOffset(colX[0] + 10f, baseY - dy);
        content.showText(line);
        content.endText();
        dy += DETAIL_LEADING;
    }
    // restaurar fuente/color para el resto de columnas
    content.setNonStrokingColor(TEXT_DARK);
    content.setFont(PDType1Font.HELVETICA, 10.5f);
}

// 2) Cantidad
drawRightAlignedText(content, qtyStr, colX[1], colX[2] - 10f, baseY);

// 3) Costo Unitario
drawRightAlignedText(content, unitCostStr, colX[2], colX[3] - 10f, baseY);

// 4) Total
drawRightAlignedText(content, lineTotalStr, colX[3], colX[4] - 10f, baseY);

// Bordes
content.setStrokingColor(LIGHT_BLUE);
content.setLineWidth(0.6f);
content.addRect(tableLeft, y - rowHeight, tableWidth, rowHeight);
content.stroke();

y -= rowHeight;
rowIndex++;


                }
            }

            // ==== TOTAL GENERAL ====
            float footerRowH = baseRowHeight;
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.setNonStrokingColor(new Color(250, 252, 255));
            content.addRect(colX[2], y - footerRowH, colWidths[2] + colWidths[3], footerRowH);
            content.fill();

            content.setStrokingColor(LIGHT_BLUE);
            content.setLineWidth(1.0f);
            content.addRect(colX[2], y - footerRowH, colWidths[2] + colWidths[3], footerRowH);
            content.stroke();

            content.setNonStrokingColor(TEXT_DARK);
            content.beginText();
            content.newLineAtOffset(colX[2] + 10f, y - 15);
            content.showText("TOTAL");
            content.endText();

            String grandTotalStr = money.format(grandTotal);
            drawRightAlignedText(content, grandTotalStr, colX[3], colX[4] - 10f, y - 15);
            y -= footerRowH + 10;
        }

        document.save(filePath);
    }

    return filePath;
}


/* =================== helpers finos =================== */

private static String safe(String v) {
    return v == null ? "—" : v;
}

private static void drawRightAlignedText(PDPageContentStream content, String text, float xLeft, float xRight, float baselineY) throws IOException {
    float textWidth = PDType1Font.HELVETICA.getStringWidth(text) / 1000f * 10.5f; // fuente normal 10.5
    float x = Math.max(xLeft, xRight - textWidth);
    content.beginText();
    content.setFont(PDType1Font.HELVETICA, 10.5f);
    content.newLineAtOffset(x, baselineY);
    content.showText(text);
    content.endText();
}

/**
 * Recorta texto para que quepa en el ancho (sencillo, una línea).
 */
private static String trimToFit(String text, int pxWidth, PDFont font, float fontSize) throws IOException {
    if (text == null) return "";
    float maxWidth = pxWidth - 16f; // padding aproximado
    float width = font.getStringWidth(text) / 1000f * fontSize;
    if (width <= maxWidth) return text;
    String ell = "…";
    for (int i = text.length() - 1; i > 0; i--) {
        String s = text.substring(0, i) + ell;
        float w = font.getStringWidth(s) / 1000f * fontSize;
        if (w <= maxWidth) return s;
    }
    return ell;
}


// Calcula el ancho en puntos de un texto con la tipografía/tamaño dados
private static float textWidth(String text, PDFont font, float fontSize) throws IOException {
    if (text == null || text.isEmpty()) return 0f;
    return font.getStringWidth(text) / 1000f * fontSize;
}


// Envuelve el texto en múltiples líneas para que no supere maxWidth (puntos)
private static java.util.List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
    java.util.List<String> lines = new java.util.ArrayList<>();
    if (text == null || text.isBlank()) return lines;

    String[] words = text.trim().split("\\s+");
    StringBuilder current = new StringBuilder();

    for (String w : words) {
        String trial = current.length() == 0 ? w : current + " " + w;
        if (textWidth(trial, font, fontSize) <= maxWidth) {
            current.setLength(0);
            current.append(trial);
        } else {
            if (current.length() > 0) lines.add(current.toString());
            // Si la palabra sola es más ancha que maxWidth, hacemos un corte “duro”
            if (textWidth(w, font, fontSize) > maxWidth) {
                String cut = w;
                String acc = "";
                for (int i = 0; i < cut.length(); i++) {
                    String t = acc + cut.charAt(i);
                    if (textWidth(t, font, fontSize) <= maxWidth) {
                        acc = t;
                    } else {
                        if (!acc.isEmpty()) lines.add(acc);
                        acc = String.valueOf(cut.charAt(i));
                    }
                }
                current.setLength(0);
                current.append(acc);
            } else {
                current.setLength(0);
                current.append(w);
            }
        }
    }
    if (current.length() > 0) lines.add(current.toString());
    return lines;
}









}
