package com.springweb.catalog.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springweb.catalog.domain.InventoryLedger;

public interface InventoryLedgerRepository extends JpaRepository<InventoryLedger, Long> {

}
