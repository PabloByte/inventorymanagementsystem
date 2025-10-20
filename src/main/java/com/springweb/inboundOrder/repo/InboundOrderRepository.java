package com.springweb.inboundOrder.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springweb.inboundOrder.domain.InboundOrder;

public interface  InboundOrderRepository  extends JpaRepository<InboundOrder, Long>{


  @Query("""
    SELECT COUNT(o)
    FROM InboundOrder o
    WHERE o.createdAt >= :startOfDay AND o.createdAt < :endOfDay
""")
long countInboundOrdersToday(@Param("startOfDay") LocalDateTime startOfDay,@Param("endOfDay") LocalDateTime endOfDay);

Optional<InboundOrder> findByOrderNumber(String orderNumber);     

@Query("select new com.springweb.inboundOrder.dto.InboundOrderPendingSelectDto(o.orderNumber,o.id,o.supplier.name,o.warehouse.name,o.warehouse.code) from InboundOrder o where o.status = com.springweb.inboundOrder.domain.InboundOrder.InboundStatus.PENDING order by o.createdAt asc")
List<com.springweb.inboundOrder.dto.InboundOrderPendingSelectDto> findPendingForSelect();

// 1) Header de la orden
@Query("""
       select new com.springweb.inboundOrder.dto.InboundOrderDetailHeaderDto(
           o.orderNumber,
           cast(o.status as string),
           o.supplier.name,
           o.warehouse.id,
           o.warehouse.name,
           o.warehouse.code
       )
       from InboundOrder o
       where o.orderNumber = :orderNumber
       """)
com.springweb.inboundOrder.dto.InboundOrderDetailHeaderDto
findDetailHeaderByOrderNumber(String orderNumber);





// 2) Ítems agregados por producto (ordered / receivedSoFar / unitCost sugerido)
@Query("""
    select new com.springweb.inboundOrder.dto.InboundOrderDetailItemDto(
        p.id,
        p.sku,
        p.name,
        sum(oi.quantity),
        coalesce((
            select sum(ri.quantityReceived)
            from InboundReceipt r
            join r.items ri
            where r.inboundOrder.id = o.id
              and ri.product.id = p.id
        ), 0L),
        coalesce(max(oi.unitCost), p.price)
    )
    from InboundOrder o
    join o.orderItems oi
    join oi.product p
    where o.orderNumber = :orderNumber
    group by p.id, p.sku, p.name, p.price, o.id
    order by p.name asc
    """)
java.util.List<com.springweb.inboundOrder.dto.InboundOrderDetailItemDto>
findDetailItemsByOrderNumber(String orderNumber);

    @Query("""
        select distinct o
        from InboundOrder o
        left join fetch o.supplier s
        left join fetch o.warehouse w
        left join fetch o.orderItems oi
        left join fetch oi.product p
        where o.id = :id
        """)
    Optional<InboundOrder> findByIdWithItemsAndRefs(@Param("id") Long id);













  

}
