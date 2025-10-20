package com.springweb.catalog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.springweb.catalog.domain.Category;
import com.springweb.catalog.domain.Product;
import com.springweb.catalog.dto.ProductDtoForHtmlInsert;
import com.springweb.catalog.dto.ProductDtoInsert;
import com.springweb.catalog.dto.ProductDtoReturn;
import com.springweb.catalog.repo.CategoryRepository;
import com.springweb.catalog.repo.ProductRepository;
import com.springweb.shared.mapper.InventoryManagementMapper;
import com.springweb.suppliers.domain.Supplier;
import com.springweb.suppliers.repo.SupplierRepository;

@Service
public class ProductServiceImpl implements IProductService {

  private final InventoryManagementMapper mapper;

  private final ProductRepository productRepository;

  private final CategoryRepository categoryRepository;
  private final SupplierRepository supplierRepository;

 

  public ProductServiceImpl(InventoryManagementMapper mapper, ProductRepository productRepository,
      CategoryRepository categoryRepository, SupplierRepository supplierRepository) {
    this.mapper = mapper;
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.supplierRepository = supplierRepository;
  }

  Category validateCategory (Long id ){

      return  categoryRepository.findById(id)
    .orElseThrow(()-> new  RuntimeException("Category selected not found"));

  }

  Supplier validateSupplier (Long id ){
    return  supplierRepository.findById(id)
    .orElseThrow(()-> new RuntimeException("Not found the Supplier inserted "));
  }


@Override
@Transactional
public ProductDtoReturn createProduct(ProductDtoInsert dto) {

    // 0) Regla multi-bodega: en create NO se permite setear stock
    

    // 1) Normalización básica
    final String sku = dto.getSku().trim().toUpperCase();
    final String barcode = (dto.getBarcode() != null && !dto.getBarcode().trim().isEmpty())
            ? dto.getBarcode().trim()
            : null;
    final String name = dto.getName().trim();
    final String description = (dto.getDescription() != null && !dto.getDescription().trim().isEmpty())
            ? dto.getDescription().trim()
            : null;

    // 2) Validaciones de unicidad
    if (productRepository.existsBySku(sku)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "El SKU ya existe.");
    }
    if (barcode != null && productRepository.existsByBarcode(barcode)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "El barcode ya existe.");
    }

    // 3) Validaciones de relaciones
    final Category category = validateCategory(dto.getCategoryId());   // lanza 404 si no existe
    final Supplier supplier = validateSupplier(dto.getSupplierId());   // lanza 404 si no existe

    // 4) Construcción de entidad (stock queda en 0: caché derivada del Stock por bodega)
    final Product p = new Product();
    p.setSku(sku);
    p.setBarcode(barcode);
    p.setName(name);
    p.setDescription(description);
    p.setPrice(dto.getPrice());
    p.setReorderPoint(dto.getReorderPoint());
    p.setStatus(Product.ProductStatus.valueOf(dto.getStatus().toUpperCase())); // ACTIVE | INACTIVE
    p.setCategory(category);
    p.setSupplier(supplier);

    p.setSerial(dto.getSerial());
    p.setLote(dto.getLote());
    p.setDimensiones(dto.getDimensiones());
    p.setPeso(dto.getPeso());
    p.setObservacion(dto.getObservacion());


    String estadoCert = Optional.ofNullable(dto.getEstadoCertificado())
    .map(String::trim)
    .filter(s -> !s.isEmpty())
    .map(String::toUpperCase)
    .orElse(null);


    p.setStock(0); // <- NO se toma del DTO. Se actualizará vía movimientos de inventario.
    p.setEstadoCertificado(Product.EstadoCertificado.valueOf(estadoCert));

    // 5) Persistencia
    final Product saved = productRepository.save(p);

    // 6) Respuesta
    return mapper.productToProductDtoReturn(saved);
}




  @Override
  public List<ProductDtoForHtmlInsert> showProductsByName(){

    return productRepository.showProductByName();

  }

     @Override
     @Transactional(readOnly= true)
  public List<?> listForReorder(Integer thresholdOpt){

          if (thresholdOpt == null || thresholdOpt == 0) {
        // Usa el ROP de cada producto, consulta en DB
        return productRepository.findReorderByOwnROP();
    }
    if (thresholdOpt < 0) {
        throw new IllegalArgumentException("threshold must be superior to 0");
    }
  
    return  productRepository.findReorderByGlobalThreshold(thresholdOpt);


  }


  //ya funcionando
   @Override
  public List<ProductDtoReturn> showAllProducts() {
  List<Product> allProducts = productRepository.findAllWithCategoryAndSupplier();

  return mapper.listProductToListProductDtoReturn(allProducts);

  }


@Override
@Transactional
public ProductDtoReturn updateProduct(Long id, ProductDtoInsert dto) {

    // 0) Regla multi-bodega: en update NO se permite setear stock
   

    // 1) Obtener producto o 404
    Product p = productRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "El producto no se encuentra en la base de datos"));

    // 2) Normalizar / validar unicidad (excluyendo el propio id)
    final String sku = dto.getSku().trim().toUpperCase();
    final String barcode = (dto.getBarcode() != null && !dto.getBarcode().isBlank())
            ? dto.getBarcode().trim()
            : null;

    if (productRepository.existsBySkuAndIdNot(sku, id)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "El SKU ya existe.");
    }
    if (barcode != null && productRepository.existsByBarcodeAndIdNot(barcode, id)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "El barcode ya existe.");
    }

     String estadoCert = Optional.ofNullable(dto.getEstadoCertificado())
    .map(String::trim)
    .filter(s -> !s.isEmpty())
    .map(String::toUpperCase)
    .orElse(null);

    // 3) Resolver relaciones
    Category category = validateCategory(dto.getCategoryId());
    Supplier supplier = validateSupplier(dto.getSupplierId());

    // 4) Mapear campos (SIN tocar el stock)
    p.setSku(sku);
    p.setBarcode(barcode);
    p.setName(dto.getName().trim());
    p.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
    p.setPrice(dto.getPrice());
    p.setReorderPoint(dto.getReorderPoint());
    p.setStatus(Product.ProductStatus.valueOf(dto.getStatus().toUpperCase()));
    p.setCategory(category);
    p.setSupplier(supplier);


       p.setSerial(dto.getSerial());
    p.setLote(dto.getLote());
    p.setDimensiones(dto.getDimensiones());
    p.setPeso(dto.getPeso());
    p.setObservacion(dto.getObservacion());

    p.setEstadoCertificado(Product.EstadoCertificado.valueOf(estadoCert));


  


    // updatedAt se setea por @PreUpdate en la entidad

    // 5) Guardar y responder
    Product saved = productRepository.save(p);
    return mapper.productToProductDtoReturn(saved);
}


  @Override
  @Transactional
  public void deleteProductById(Long id) {
         try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        } catch (DataIntegrityViolationException ex) {
            // Si hay FKs (p.ej. en órdenes), puedes optar por soft-delete con status=INACTIVE
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product cannot be deleted due to related records");
        }

  }














  @Override
  public ProductDtoReturn findProductById(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findProductById'");
  }

 


  @Override
  public List<Product> findByNameContainingIgnoreCase(String name) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findByNameContainingIgnoreCase'");
  }

  @Override
  public List<Product> findByCategoryId(Long categoryId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findByCategoryId'");
  }

  @Override
  public List<Product> findBySupplierId(Long supplierId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findBySupplierId'");
  }

  @Override
  public List<Product> findLowStockProducts(Integer threshold) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findLowStockProducts'");
  }

  

  @Transactional(readOnly = true)
  @Override
  public ProductDtoReturn getById(Long id) {

      Product p = productRepository.findWithGraphById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    return mapper.productToProductDtoReturn(p);

  }

 
















  

}
