package com.dhr.chapatumoney.repository;

import com.dhr.chapatumoney.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, UUID> {

    List<TicketType> findByEventId(UUID eventId);

    @Query("SELECT MIN(t.precio) FROM TicketType t WHERE t.event.id = :eventId")
    Optional<BigDecimal> findMinPrecioByEventId(@Param("eventId") UUID eventId);

    @Query("SELECT COALESCE(SUM(t.capacidad - t.vendidos), 0) FROM TicketType t WHERE t.event.id = :eventId")
    Integer findTotalDisponiblesByEventId(@Param("eventId") UUID eventId);
}
