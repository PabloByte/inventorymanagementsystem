package com.springweb.catalog.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springweb.catalog.domain.Product;
import com.springweb.catalog.dto.ProductDtoForHtmlInsert;
import com.springweb.catalog.dto.ProductDtoInsert;
import com.springweb.catalog.dto.ProductDtoReturn;
import com.springweb.catalog.service.ProductServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductServiceImpl productServiceImpl;

    public ProductController(ProductServiceImpl productServiceImpl) {
        this.productServiceImpl = productServiceImpl;
    }


    @PostMapping
    public ResponseEntity<?> createProduct (@Valid @RequestBody ProductDtoInsert product ){

        ProductDtoReturn product1 = productServiceImpl.createProduct(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(product1);
    }

    // PUT /api/products/{id}  -> Actualiza un producto y devuelve el DTO actualizado
@PutMapping("/{id}")
public ResponseEntity<ProductDtoReturn> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDtoInsert dto) {

    ProductDtoReturn updated = productServiceImpl.updateProduct(id, dto);
    return ResponseEntity.ok(updated); // 200 OK
}

// DELETE /api/products/{id}  -> Elimina un producto (o 404 si no existe)
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productServiceImpl.deleteProductById(id);
    return ResponseEntity.noContent().build(); // 204 No Content
}


    @GetMapping("/showProductsByName")
   public  ResponseEntity<?> showProductsByName (){

        List<ProductDtoForHtmlInsert> productsListName = productServiceImpl.showProductsByName();

        return  ResponseEntity.status(HttpStatus.OK).body(productsListName);
    }

    
    // GET /api/products/reorder
    // GET /api/products/reorder?threshold=5
    @GetMapping("/reorder")
     public ResponseEntity<List<?>> listForReorder( @RequestParam(name = "threshold", required = false) Integer threshold) {
 return ResponseEntity.ok(productServiceImpl.listForReorder(threshold)); // 200 con [] si no hay resultados (perfecto para frontend)
    }



    @GetMapping("/showAllProducts")
    public ResponseEntity<?> showAllProducts (){
        List<ProductDtoReturn> list =  productServiceImpl.showAllProducts();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(list);
    }



     @GetMapping("/{id}")
  public ResponseEntity<ProductDtoReturn> getById(@PathVariable Long id) {
    return ResponseEntity.ok(productServiceImpl.getById(id));
  }

















    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        return productServiceImpl.findByNameContainingIgnoreCase(name);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productServiceImpl.findByCategoryId(categoryId);
    }

    @GetMapping("/supplier/{supplierId}")
    public List<Product> getProductsBySupplier(@PathVariable Long supplierId) {
        return productServiceImpl.findBySupplierId(supplierId);
    }













    
}
