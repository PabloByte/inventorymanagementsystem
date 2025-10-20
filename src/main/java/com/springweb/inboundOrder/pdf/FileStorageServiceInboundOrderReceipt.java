package com.springweb.inboundOrder.pdf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.springweb.inboundOrder.domain.InboundReceipt;
import com.springweb.inboundOrder.domain.InboundReceiptItem;
import com.springweb.inboundOrder.event.InboundReceiptCreatedEvent;
import com.springweb.inboundOrder.repo.InboundReceiptRepository;

@Service
public class FileStorageServiceInboundOrderReceipt {

    private final InboundReceiptRepository receiptRepo;

    public FileStorageServiceInboundOrderReceipt(InboundReceiptRepository receiptRepo) {
        this.receiptRepo = receiptRepo;
    }

    // ====== Colores (tema naranja) ======
    private static final Color ORANGE = new Color(242, 133, 0);        // naranja principal
    private static final Color ORANGE_SOFT = new Color(255, 236, 214); // fondo suave para headers/stripes
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color BORDER = new Color(180, 180, 180);

    // ====== Fuentes ======
    private static final org.apache.pdfbox.pdmodel.font.PDFont FONT = PDType1Font.HELVETICA;
    private static final org.apache.pdfbox.pdmodel.font.PDFont FONT_B = PDType1Font.HELVETICA_BOLD;

    // ====== Formatos ======
    private static final Locale LOCALE_CO = new Locale("es", "CO");
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(LOCALE_CO);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ====== Márgenes y tamaños ======
    private static final float MARGIN = 36f;        // 0.5"
    private static final float HEADER_H = 70f;
    private static final float FOOTER_H = 30f;
    private static final float TABLE_HEADER_H = 22f;
    private static final float ROW_H = 18f;

    /**
     * Genera y devuelve el PDF como bytes, cargando el recibo con @EntityGraph.
     */
    public byte[] generatePdf(Long receiptId) {
        InboundReceipt receipt = receiptRepo.findWithGraphById(receiptId)
                .orElseThrow(() -> new IllegalArgumentException("InboundReceipt no encontrado: " + receiptId));
        return generatePdf(receipt);
    }

    /**
     * Genera el PDF a partir de la entidad ya cargada.
     */
    public byte[] generatePdf(InboundReceipt receipt) {
        Objects.requireNonNull(receipt, "receipt no puede ser null");

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDRectangle media = page.getMediaBox();
            float width = media.getWidth();
            float height = media.getHeight();

            float y = height - MARGIN;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                // Encabezado naranja
                drawHeader(cs, width, y, receipt);
                y -= (HEADER_H + 12f);

                // Bloques: Proveedor / Bodega
                y = drawBlocks(cs, width, y, receipt);

                // Datos de orden / recibo
                y -= 10f;
                y = drawReceiptOrderMeta(cs, width, y, receipt);

                // Tabla de ítems
                y -= 12f;
                y = drawItemsTable(doc, cs, page, width, height, y, receipt.getItems());

                // Observaciones + firmas
                y -= 14f;
                y = drawNotesAndSign(cs, width, y, receipt);

                // Footer
                drawFooter(cs, width);
            }

            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generando PDF de InboundReceipt " + receipt.getId(), e);
        }
    }

    /**
     * Guarda el PDF en disco y devuelve la ruta (ajústalo a S3 u otro storage si deseas).
     */
    public String generateAndStore(Long receiptId) {
        byte[] pdf = generatePdf(receiptId);
        String fileName = "INBOUND_RECEIPT_" + receiptId + "_" + System.currentTimeMillis() + ".pdf";
        File out = new File("files/inbound-receipts/" + receiptId);
        if (!out.exists()) out.mkdirs();
        File target = new File(out, fileName);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(target)) {
            fos.write(pdf);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando PDF en disco", e);
        }
        return target.getAbsolutePath();
    }

    // ====== Listener post-commit (si disparas el evento tras crear el recibo) ======
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReceiptCreated(InboundReceiptCreatedEvent evt) {
        try {
            String path = generateAndStore(evt.receiptId());
            // log/auditar si deseas
        } catch (Exception ex) {
            // log error y permitir reintento manual
        }
    }

    // =================== DIBUJO: LAYOUT ===================

    private void drawHeader(PDPageContentStream cs, float width, float topY, InboundReceipt r) throws IOException {
        float x = MARGIN;
        float w = width - 2 * MARGIN;

        // Banda naranja
        cs.setNonStrokingColor(ORANGE);
        cs.addRect(x, topY - HEADER_H, w, HEADER_H);
        cs.fill();

        // Título
        String title = "RECIBO DE ENTRADA";
        cs.beginText();
        cs.setNonStrokingColor(Color.WHITE);
        cs.setFont(FONT_B, 18);
        cs.newLineAtOffset(x + 14, topY - 28);
        cs.showText(title);
        cs.endText();

        // Número de recibo
        String num = "Recibo: " + safe(r.getReceiptNumber());
        cs.beginText();
        cs.setFont(FONT, 12);
        cs.newLineAtOffset(x + 14, topY - 50);
        cs.showText(num);
        cs.endText();

        // Número de Orden
        String orderNo = r.getInboundOrder() != null ? safe(r.getInboundOrder().getOrderNumber()) : "-";
        cs.beginText();
        cs.setFont(FONT, 12);
        cs.newLineAtOffset(x + w - 220, topY - 28);
        cs.showText("Orden: " + orderNo);
        cs.endText();

        // Fecha creación
        String createdAt = r.getCreatedAt() != null ? r.getCreatedAt().format(DTF) : "-";
        cs.beginText();
        cs.setFont(FONT, 12);
        cs.newLineAtOffset(x + w - 220, topY - 50);
        cs.showText("Creado: " + createdAt);
        cs.endText();
    }

    private float drawBlocks(PDPageContentStream cs, float width, float y, InboundReceipt r) throws IOException {
        float x = MARGIN;
        float w = width - 2 * MARGIN;
        float blockH = 60f;

        float colW = (w - 10f) / 2f;

        // Fondos
        cs.setNonStrokingColor(ORANGE_SOFT);
        cs.addRect(x, y - blockH, colW, blockH);
        cs.addRect(x + colW + 10f, y - blockH, colW, blockH);
        cs.fill();

        // Bordes
        cs.setStrokingColor(BORDER);
        cs.addRect(x, y - blockH, colW, blockH);
        cs.addRect(x + colW + 10f, y - blockH, colW, blockH);
        cs.stroke();

        // Proveedor
        cs.setNonStrokingColor(TEXT_DARK);
        cs.beginText();
        cs.setFont(FONT_B, 11);
        cs.newLineAtOffset(x + 8, y - 16);
        cs.showText("Proveedor");
        cs.endText();

        String supplierName = "-";
        String supplierNit = "-";
        if (r.getInboundOrder() != null && r.getInboundOrder().getSupplier() != null) {
            supplierName = safe(r.getInboundOrder().getSupplier().getName());
            // supplierNit si lo tienes
        }

        cs.beginText();
        cs.setFont(FONT, 10);
        cs.newLineAtOffset(x + 8, y - 32);
        cs.showText(supplierName);
        cs.endText();
        cs.beginText();
        cs.setFont(FONT, 10);
        cs.newLineAtOffset(x + 8, y - 48);
        cs.showText("NIT: " + supplierNit);
        cs.endText();

        // Bodega
        cs.beginText();
        cs.setFont(FONT_B, 11);
        cs.newLineAtOffset(x + colW + 18, y - 16);
        cs.showText("Bodega destino");
        cs.endText();

        String whName = r.getWarehouse() != null ? safe(valueOr(r.getWarehouse().getName(), "-")) : "-";
        String createdBy = safe(valueOr(r.getCreatedBy(), "-"));

        cs.beginText();
        cs.setFont(FONT, 10);
        cs.newLineAtOffset(x + colW + 18, y - 32);
        cs.showText(whName);
        cs.endText();
        cs.beginText();
        cs.setFont(FONT, 10);
        cs.newLineAtOffset(x + colW + 18, y - 48);
        cs.showText("Recibido por: " + createdBy);
        cs.endText();

        return y - blockH - 10f;
    }

    private float drawReceiptOrderMeta(PDPageContentStream cs, float width, float y, InboundReceipt r) throws IOException {
        float x = MARGIN;
        float w = width - 2 * MARGIN;

        // Franja
        cs.setNonStrokingColor(ORANGE_SOFT);
        cs.addRect(x, y - 24, w, 24);
        cs.fill();
        cs.setStrokingColor(BORDER);
        cs.addRect(x, y - 24, w, 24);
        cs.stroke();

        cs.setNonStrokingColor(TEXT_DARK);
        cs.beginText();
        cs.setFont(FONT_B, 11);
        cs.newLineAtOffset(x + 8, y - 16);
        cs.showText("Detalle del recibo");
        cs.endText();

        String orderNo = (r.getInboundOrder() != null) ? safe(r.getInboundOrder().getOrderNumber()) : "-";
        String totalRec = String.valueOf(valueOr(r.getTotalReceived(), 0));

        cs.beginText();
        cs.setFont(FONT, 10);
        cs.newLineAtOffset(x + w - 250, y - 16);
        cs.showText("Orden: " + orderNo + "   |   Total recibido: " + totalRec);
        cs.endText();

        return y - 30f;
    }

  private float drawItemsTable(PDDocument doc,
                             PDPageContentStream cs,
                             PDPage page,
                             float width,
                             float height,
                             float y,
                             List<InboundReceiptItem> items) throws IOException {

    final float x = MARGIN;
    final float w = width - 2 * MARGIN;

    // ===== Encabezado de tabla =====
    cs.setNonStrokingColor(ORANGE);
    cs.addRect(x, y - TABLE_HEADER_H, w, TABLE_HEADER_H);
    cs.fill();
    cs.setStrokingColor(BORDER);
    cs.addRect(x, y - TABLE_HEADER_H, w, TABLE_HEADER_H);
    cs.stroke();

    cs.setNonStrokingColor(Color.WHITE);
    cs.setFont(FONT_B, 10);
    // Cols: Código, Producto (2 líneas), Dimensiones, Peso, Recibido, Solicitado, Pendiente, Subtotal
    final String[] headers = new String[]{
            "Código", "Producto", "Dimensiones", "Peso", "Recibido", "Solicitado", "Pendiente", "Subtotal"
    };

    // Offsets SEGUROS dentro de A4 (w ≈ 523pt). El último queda en ~x+480.
    final float[] offsets = new float[]{
            22,   // Código
            70,   // Producto
            215,  // Dimensiones
            270,  // Peso
            315,  // Recibido
            365,  // Solicitado
            420,  // Pendiente
            480   // Subtotal
    };

    // pinta "#"
    cs.beginText(); cs.newLineAtOffset(x + 6, y - 15); cs.showText("#"); cs.endText();
    for (int i = 0; i < headers.length; i++) {
        cs.beginText(); cs.newLineAtOffset(x + offsets[i], y - 15); cs.showText(headers[i]); cs.endText();
    }
    y -= TABLE_HEADER_H;

    // ===== Filas =====
    BigDecimal totalRecibido = BigDecimal.ZERO;
    int index = 1;
    final int baseFont = 10;
    final float lineH = 12f;       // interlineado para el nombre (producto)
    final float minRowH = ROW_H;   // altura mínima de fila

    for (InboundReceiptItem it : items) {
        // --- Construcción de datos defensiva ---
        String codigo = "-";
        String name   = "-";
        if (it.getProduct() != null) {
            String sku = safe(it.getProduct().getSku());
            if (sku != null) codigo = sku; // usamos SKU como "Código"
            String n = safe(it.getProduct().getName());
            if (n != null && !n.isBlank()) name = n;
        }
        String dims  = safeDims(it.getProduct());   // Dimensiones
        String peso  = safeWeight(it.getProduct()); // Peso

        Integer qtyRec  = valueOr(getIntSafely(it.getQuantityReceived()), 0);
        Integer qtyReq  = valueOr(getIntSafely(it.getExpectedQty()), 0);
        Integer qtyPend = Math.max(qtyReq - qtyRec, 0);

        BigDecimal price = getBigDecSafely(it.getUnitCost());
        BigDecimal subtotal = null;
        if (price != null) {
            BigDecimal base = price.multiply(new BigDecimal(qtyRec));
            subtotal = base; // sin IVA en columnas (para ahorrar ancho). Si deseas, súmalo aquí.
            totalRecibido = totalRecibido.add(subtotal);
        }

        // --- Wrap del nombre a MÁXIMO 2 líneas ---
        float productMaxWidth = offsets[2] - offsets[1] - 6; // ancho entre "Producto" y "Dimensiones"
        List<String> nameLines = wrapToLines(FONT, baseFont, name, productMaxWidth, 2);
        int linesCount = Math.max(1, nameLines.size());

        // Altura de fila dinámica (mínimo ROW_H; si hay 2 líneas, crece)
        float rowH = Math.max(minRowH, 10 + (linesCount * lineH));

        // --- Salto de página si no cabe la fila + footer reservado ---
        if (y < MARGIN + FOOTER_H + rowH) {
            cs.close();

            PDPage newPage = new PDPage(PDRectangle.A4);
            doc.addPage(newPage);
            page = newPage;
            y = page.getMediaBox().getHeight() - MARGIN;
            cs = new PDPageContentStream(doc, page);

            // Redibuja encabezados en nueva página
            cs.setNonStrokingColor(ORANGE);
            cs.addRect(x, y - TABLE_HEADER_H, w, TABLE_HEADER_H); cs.fill();
            cs.setStrokingColor(BORDER);
            cs.addRect(x, y - TABLE_HEADER_H, w, TABLE_HEADER_H); cs.stroke();

            cs.setNonStrokingColor(Color.WHITE);
            cs.setFont(FONT_B, 10);
            cs.beginText(); cs.newLineAtOffset(x + 6, y - 15); cs.showText("#"); cs.endText();
            for (int i = 0; i < headers.length; i++) {
                cs.beginText(); cs.newLineAtOffset(x + offsets[i], y - 15); cs.showText(headers[i]); cs.endText();
            }
            y -= TABLE_HEADER_H;
        }

        // Fondo alterno
        if (index % 2 == 0) {
            cs.setNonStrokingColor(ORANGE_SOFT);
            cs.addRect(x, y - rowH, w, rowH); cs.fill();
        }

        // Borde fila
        cs.setStrokingColor(BORDER);
        cs.addRect(x, y - rowH, w, rowH); cs.stroke();

        // Contenido
        cs.setNonStrokingColor(TEXT_DARK);
        cs.setFont(FONT, baseFont);

        // Número de línea
        text(cs, x + 6, y - 12, FONT, baseFont, String.valueOf(index));

        // Código
        text(cs, x + offsets[0], y - 12, FONT, baseFont, codigo);

        // Producto (puede ocupar 2 líneas)
        float curY = y - 12;
        for (String ln : nameLines) {
            text(cs, x + offsets[1], curY, FONT, baseFont, ln);
            curY -= lineH;
        }

        // Dimensiones, Peso
        textTruncated(cs, x + offsets[2], y - 12, FONT, baseFont, dims, (offsets[3] - offsets[2] - 6));
        text(cs,        x + offsets[3], y - 12, FONT, baseFont, peso);

        // Cantidades
        text(cs, x + offsets[4], y - 12, FONT, baseFont, String.valueOf(qtyRec));
        text(cs, x + offsets[5], y - 12, FONT, baseFont, (qtyReq > 0 ? String.valueOf(qtyReq) : "-"));
        text(cs, x + offsets[6], y - 12, FONT, baseFont, (qtyReq > 0 ? String.valueOf(qtyPend) : "-"));

        // Subtotal (alineado al último offset; siempre dentro del A4)
        text(cs, x + offsets[7], y - 12, FONT, baseFont, (subtotal != null ? CURRENCY.format(subtotal) : "-"));

        y -= rowH;
        index++;
    }

    // ===== Totales (por recibido) =====
    y -= 6;
    cs.setStrokingColor(BORDER);
    cs.addRect(x, y - ROW_H, w, ROW_H); cs.stroke();
    cs.setNonStrokingColor(TEXT_DARK);
    text(cs, x + 6,          y - 12, FONT_B, 10, "Total recibido");
    text(cs, x + 480,        y - 12, FONT_B, 10, CURRENCY.format(totalRecibido)); // usa el último offset (480)

    return y - ROW_H;
}


    private float drawNotesAndSign(PDPageContentStream cs, float width, float y, InboundReceipt r) throws IOException {
        float x = MARGIN;
        float w = width - 2 * MARGIN;

        String note = safe(valueOr(r.getNote(), ""));
        if (!note.isBlank()) {
            cs.setNonStrokingColor(ORANGE_SOFT);
            cs.addRect(x, y - 60, w, 60);
            cs.fill();
            cs.setStrokingColor(BORDER);
            cs.addRect(x, y - 60, w, 60);
            cs.stroke();

            text(cs, x + 8, y - 16, FONT_B, 11, "Observaciones");
            textWrapped(cs, x + 8, y - 32, FONT, 10, note, w - 16, 12);

            y -= 70f;
        }

        float boxW = (w - 10) / 2f;
        float boxH = 50f;
        cs.setStrokingColor(BORDER);

        cs.addRect(x, y - boxH, boxW, boxH);
        cs.addRect(x + boxW + 10, y - boxH, boxW, boxH);
        cs.stroke();

        text(cs, x + 8, y - 16, FONT_B, 10, "Recibe:");
        text(cs, x + boxW + 18, y - 16, FONT_B, 10, "Verifica:");

        return y - (boxH + 6);
    }

    private void drawFooter(PDPageContentStream cs, float width) throws IOException {
        float x = MARGIN;
        cs.setStrokingColor(BORDER);
        cs.moveTo(x, MARGIN + FOOTER_H);
        cs.lineTo(width - MARGIN, MARGIN + FOOTER_H);
        cs.stroke();

        text(cs, x, MARGIN + FOOTER_H - 14, FONT, 9, "Documento generado automáticamente - Sistema de Inventarios");
    }

    // =================== HELPERS ===================

    private static String safe(String s) {
        return s == null ? null : s.replaceAll("\\s+", " ").trim();
    }

    private static <T> T valueOr(T val, T def) {
        return val == null ? def : val;
    }

    private static Integer getIntSafely(Integer v) { return v; }

    private static BigDecimal getBigDecSafely(BigDecimal v) { return v; }

    private static String ivaToPct(BigDecimal iva) {
        return iva.movePointRight(2).stripTrailingZeros().toPlainString() + "%";
    }

    private static void text(PDPageContentStream cs, float x, float y, org.apache.pdfbox.pdmodel.font.PDFont font, int size, String txt) throws IOException {
        cs.beginText();
        cs.setNonStrokingColor(TEXT_DARK);
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(txt == null ? "-" : txt);
        cs.endText();
    }

    private static void textTruncated(PDPageContentStream cs, float x, float y, org.apache.pdfbox.pdmodel.font.PDFont font, int size, String txt, float maxWidth) throws IOException {
        if (txt == null) txt = "-";
        String out = txt;
        float w = font.getStringWidth(out) / 1000 * size;
        while (w > maxWidth && out.length() > 3) {
            out = out.substring(0, out.length() - 4) + "...";
            w = font.getStringWidth(out) / 1000 * size;
        }
        text(cs, x, y, font, size, out);
    }

    private static void textWrapped(PDPageContentStream cs, float x, float y, org.apache.pdfbox.pdmodel.font.PDFont font, int size, String txt, float maxWidth, float lineH) throws IOException {
        if (txt == null || txt.isBlank()) return;
        String[] words = txt.split("\\s+");
        StringBuilder line = new StringBuilder();
        float curY = y;
        for (String w : words) {
            String test = (line.length() == 0 ? w : line + " " + w);
            float tw = font.getStringWidth(test) / 1000 * size;
            if (tw < maxWidth) {
                line = new StringBuilder(test);
            } else {
                text(cs, x, curY, font, size, line.toString());
                curY -= lineH;
                line = new StringBuilder(w);
            }
        }
        if (!line.isEmpty()) {
            text(cs, x, curY, font, size, line.toString());
        }
    }

    private static float[] colWidths(float tableWidth) {
        return new float[]{32, 120, 60, 60, 60, 70, 70, 60, 80};
    }

    // ==== NUEVOS HELPERS PARA U.M, DIMENSIONES Y PESO ====
    private static String safeUom(Object product) {
        if (product == null) return "-";
        try { return String.valueOf(product.getClass().getMethod("getUom").invoke(product)); } catch (Exception ignore) {}
        try { return String.valueOf(product.getClass().getMethod("getUnit").invoke(product)); } catch (Exception ignore) {}
        try { return String.valueOf(product.getClass().getMethod("getUnidad").invoke(product)); } catch (Exception ignore) {}
        return "-";
    }

    private static String safeDims(Object product) {
        if (product == null) return "-";
        // Si existe getDimensions() (texto), úsalo
        try {
            Object d = product.getClass().getMethod("getDimensions").invoke(product);
            if (d != null) {
                String s = d.toString().trim();
                if (!s.isBlank()) return s;
            }
        } catch (Exception ignore) {}

        // Intenta largo x ancho x alto
        String l = null, w = null, h = null;
        try { l = Objects.toString(product.getClass().getMethod("getLength").invoke(product), null); } catch (Exception ignore) {}
        try { w = Objects.toString(product.getClass().getMethod("getWidth").invoke(product),  null); } catch (Exception ignore) {}
        try { h = Objects.toString(product.getClass().getMethod("getHeight").invoke(product), null); } catch (Exception ignore) {}

        if (l != null || w != null || h != null) {
            return (l != null ? l : "-") + "x" + (w != null ? w : "-") + "x" + (h != null ? h : "-");
        }
        return "-";
    }

    private static String safeWeight(Object product) {
        if (product == null) return "-";
        Object val = null;
        try { val = product.getClass().getMethod("getWeight").invoke(product); } catch (Exception ignore) {}
        if (val == null) try { val = product.getClass().getMethod("getPeso").invoke(product); } catch (Exception ignore) {}
        if (val == null) try { val = product.getClass().getMethod("getWeightKg").invoke(product); } catch (Exception ignore) {}

        if (val == null) return "-";

        if (val instanceof Number) {
            double d = ((Number) val).doubleValue();
            return (d == Math.rint(d)) ? ((long) d) + " kg" : String.format(java.util.Locale.US, "%.2f kg", d);
        }
        String s = val.toString().trim();
        if (s.matches(".*[a-zA-Z].*")) return s; // ya incluye unidad
        return s + " kg";
    }


    private static List<String> wrapToLines(org.apache.pdfbox.pdmodel.font.PDFont font,
                                        int fontSize,
                                        String text,
                                        float maxWidth,
                                        int maxLines) throws IOException {
    if (text == null) text = "-";
    List<String> out = new ArrayList<>();
    String[] words = text.trim().split("\\s+");
    StringBuilder line = new StringBuilder();

    for (String w : words) {
        String test = line.length() == 0 ? w : line + " " + w;
        float tw = font.getStringWidth(test) / 1000 * fontSize;
        if (tw <= maxWidth) {
            line = new StringBuilder(test);
        } else {
            if (line.length() > 0) out.add(line.toString());
            line = new StringBuilder(w);
            if (out.size() >= maxLines - 1) break; // la siguiente línea será la última
        }
    }
    if (out.size() < maxLines && line.length() > 0) out.add(line.toString());

    // Si todavía sobra texto, corta y añade "..."
    if (out.size() == maxLines) {
        String last = out.get(maxLines - 1);
        while ((font.getStringWidth(last + "...") / 1000 * fontSize) > maxWidth && last.length() > 3) {
            last = last.substring(0, last.length() - 1);
        }
        out.set(maxLines - 1, last + (last.endsWith("...") ? "" : "..."));
    }
    return out.isEmpty() ? List.of("-") : out;
}












}





































