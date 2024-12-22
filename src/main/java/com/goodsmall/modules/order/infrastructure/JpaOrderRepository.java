package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface JpaOrderRepository extends JpaRepository<Order, Long> {
    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.orderProducts op
        LEFT JOIN FETCH op.product p
        WHERE o.user.id = :userId
        AND o.id<:cursor
        ORDER BY o.creatAt DESC,o.id desc
    """)
    Slice<Order> findOrdersWithProducts(@Param("userId") Long userId, Long cursor, Pageable pageable);

}
