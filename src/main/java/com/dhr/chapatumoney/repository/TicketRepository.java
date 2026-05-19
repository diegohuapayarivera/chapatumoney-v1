package com.dhr.chapatumoney.repository;

import com.dhr.chapatumoney.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByOrderId(UUID orderId);

    @Query("""
        SELECT t FROM Ticket t
        WHERE t.order.user.id = :userId
        """)
    Page<Ticket> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    boolean existsByIdAndOrderUserId(UUID id, UUID userId);
}
