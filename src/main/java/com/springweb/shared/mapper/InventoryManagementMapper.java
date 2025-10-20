package com.springweb.shared.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.springweb.catalog.domain.Category;
import com.springweb.catalog.domain.Product;
import com.springweb.catalog.dto.CategoryDtoReturn;
import com.springweb.catalog.dto.ProductDtoReturn;
import com.springweb.inboundOrder.domain.InboundOrder;
import com.springweb.inboundOrder.domain.InboundOrderItem;
import com.springweb.inboundOrder.dto.InboundOrderDtoReturn;
import com.springweb.inboundOrder.dto.InboundOrderItemDtoReturn;
import com.springweb.suppliers.domain.Supplier;
import com.springweb.suppliers.dto.SupplierDtoReturn;

@Mapper(componentModel="spring")
public interface InventoryManagementMapper {

  @Mappings({
        // Relaciones desnormalizadas (evita problemas LAZY en JSON)
        @Mapping(target = "categoryId",   source = "category.id"),
        @Mapping(target = "categoryName", source = "category.name"),
        @Mapping(target = "supplierId",   source = "supplier.id"),
        @Mapping(target = "supplierName", source = "supplier.name"),

        // Enum → String
        @Mapping(target = "status", expression = "java(product.getStatus() != null ? product.getStatus().name() : null)")

        // Campos de igual nombre se mapean solos: id, sku, barcode, name, description, price, stock,
        // reorderPoint, createdAt, updatedAt.
    })
    ProductDtoReturn productToProductDtoReturn(Product product);

    List<ProductDtoReturn> listProductToListProductDtoReturn(List<Product> products);



  CategoryDtoReturn categoryToCategoryDtoReturn (Category category);
  List<CategoryDtoReturn> categoryListToCategoryDtoReturnsList (List<Category> categoryList );

  @Mapping(target = "product", expression = "java(inboundOrderItem.getProduct() != null ? inboundOrderItem.getProduct().getName() : null)")
  InboundOrderItemDtoReturn inboundOrderItemToInboundOrderItemDtoReturn (InboundOrderItem inboundOrderItem);

  @Mapping(target = "product", expression = "java(inboundOrderItem.getProduct() != null ? inboundOrderItem.getProduct().getName() : null)")
  List<InboundOrderItemDtoReturn>  listInboundOrderItemToListInboundOrderItemDtoReturn (List<InboundOrderItem>  listInboundOrderItem);


  @Mapping(target = "supplier", expression = "java(inboundOrder.getSupplier() != null ? inboundOrder.getSupplier().getName() : null)")
  @Mapping(target = "warehouse", expression = "java(inboundOrder.getWarehouse() != null ? inboundOrder.getWarehouse().getName() : null)")
  InboundOrderDtoReturn inboundOrderToInboundOrderDtoReturn (InboundOrder inboundOrder);

  
  SupplierDtoReturn supplierToSupplierDtoReturn (Supplier supplier);


  













}
