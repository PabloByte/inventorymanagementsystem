package com.springweb.inboundOrder.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.springweb.inboundOrder.domain.InboundReceipt;

public interface InboundReceiptRepository extends  JpaRepository<InboundReceipt, Long> {



 @EntityGraph(attributePaths = {
        "inboundOrder",
        "inboundOrder.supplier",
        "warehouse",
        "items",
        "items.product"
    })
    Optional<InboundReceipt> findWithGraphById(Long id);










}
