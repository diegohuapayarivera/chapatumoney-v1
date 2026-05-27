package com.dhr.chapatumoney.repository;

import com.dhr.chapatumoney.entity.Order;
import com.dhr.chapatumoney.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
        SELECT o FROM Order o
        WHERE o.user.id = :userId
          AND (:estado IS NULL OR o.estado = :estado)
        """)
    Page<Order> findByUserIdAndEstado(
            @Param("userId") UUID userId,
            @Param("estado") OrderStatus estado,
            Pageable pageable);

    boolean existsByTicketTypeEventIdAndEstado(UUID eventId, OrderStatus estado);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.estado = :estado")
    java.math.BigDecimal sumTotalByEstado(@Param("estado") OrderStatus estado);

    @Query("SELECT COALESCE(SUM(o.cantidad), 0) FROM Order o WHERE o.estado = :estado")
    Long sumCantidadByEstado(@Param("estado") OrderStatus estado);
}
