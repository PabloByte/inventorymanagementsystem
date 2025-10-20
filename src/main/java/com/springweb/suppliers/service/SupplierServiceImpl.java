package com.springweb.suppliers.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.springweb.shared.mapper.InventoryManagementMapper;
import com.springweb.suppliers.domain.Supplier;
import com.springweb.suppliers.dto.SupplierDtoForHtmlInsert;
import com.springweb.suppliers.dto.SupplierDtoInsert;
import com.springweb.suppliers.dto.SupplierDtoReturn;
import com.springweb.suppliers.repo.SupplierRepository;

@Service
public class SupplierServiceImpl implements ISupplierService {

  private final SupplierRepository supplierRepository;
  private final InventoryManagementMapper mapper;

  public SupplierServiceImpl(SupplierRepository supplierRepository, InventoryManagementMapper mapper) {
    this.supplierRepository = supplierRepository;
    this.mapper = mapper;
  }

  @Override
  public SupplierDtoReturn createSupplier (SupplierDtoInsert dto ) {


    Supplier newSupplier = new Supplier();

    newSupplier.setName(dto.getName());
    newSupplier.setContactPerson(dto.getContactPerson());
    newSupplier.setEmail(dto.getEmail());
    newSupplier.setPhone(dto.getPhone());
    newSupplier.setAddress(dto.getAddress());
    
    supplierRepository.save(newSupplier);

    return mapper.supplierToSupplierDtoReturn(newSupplier);
  }







  @Override
  public List<SupplierDtoForHtmlInsert> showSupplierByName() {

    List<SupplierDtoForHtmlInsert> list = supplierRepository.showSupplierByName();

    return  list;
  }












  @Override
  public List<Supplier> findByNameContainingIgnoreCase(String name) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findByNameContainingIgnoreCase'");
  }

  @Override
  public List<Supplier> findByEmailContainingIgnoreCase(String email) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findByEmailContainingIgnoreCase'");
  }








  

}
