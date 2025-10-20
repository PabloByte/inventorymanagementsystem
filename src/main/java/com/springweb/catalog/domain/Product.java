package com.springweb.catalog.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.springweb.suppliers.domain.Supplier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_supplier", columnList = "supplier_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_product_sku", columnNames = {"sku"})
        // El unique de barcode (cuando no es null) se hará como índice parcial en la migración SQL
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal price;
    
   
    @Min(0)
    private Integer stock;

       // ---------- Nuevos campos ----------
    @NotBlank
    @Size(max = 32)
    @Column(nullable = false, length = 32)
    private String sku;               // Único por unique constraint de la @Table

    @Size(max = 64)
    @Column(length = 64)
    private String barcode;           // Único cuando no es null -> índice parcial en Flyway

    @NotNull
    @Min(0)
    @Column(name = "reorder_point", nullable = false)
    private Integer reorderPoint;     // Umbral para alertas de reposición

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ProductStatus status;     // ACTIVE | INACTIVE
    // -----------------------------------


    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {

           createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Defaults defensivos en caso de que vengan null desde DTO
        if (status == null) status = ProductStatus.ACTIVE;
        if (reorderPoint == null) reorderPoint = 5;
        if (sku == null || sku.isBlank()) {
            // fallback simple; en la migración asignamos SKU definitivo basado en ID
            sku = "SKU-TEMP-" + System.nanoTime();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ProductStatus { ACTIVE, INACTIVE }

        // --- Nuevos campos: trazabilidad, dimensiones/peso, certificación, observación ---

@Size(max = 100)
@Column(name = "serial", length = 100)
private String serial;


@Size(max = 50)
@Column(name = "lote", length = 50)
private String lote;
    
@Size(max = 100)
@Column(name = "dimensiones", length = 100)
private String dimensiones;   


@Digits(integer = 7, fraction = 3) // NUMERIC(10,3) -> 7 enteros + 3 decimales
@Positive(message = "El peso debe ser > 0 si se informa")
@Column(name = "peso", precision = 10, scale = 3)
private BigDecimal peso;    

  
// Enum alineado al CHECK de la BD: ('CERTIFICADO','NO_CERTIFICADO','EN_PROCESO','VENCIDO')
@Enumerated(EnumType.STRING)
@Column(name = "estado_certificado", length = 20)
private EstadoCertificado estadoCertificado;

@Column(name = "observacion", columnDefinition = "text")
private String observacion;

// --- Enum para estado_certificado ---
public enum EstadoCertificado {
    CERTIFICADO,
    NO_CERTIFICADO,
    EN_PROCESO,
    VENCIDO
}







    
  
}
