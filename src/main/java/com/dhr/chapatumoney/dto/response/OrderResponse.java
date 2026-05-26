package com.dhr.chapatumoney.dto.response;

import com.dhr.chapatumoney.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private TicketTypeResponse ticketType;
    private EventSummaryResponse evento;
    private int cantidad;
    private BigDecimal total;
    private OrderStatus estado;
    /** Populated only when estado = confirmed */
    private List<TicketResponse> tickets;
    private OffsetDateTime createdAt;
    private String numeroConadis;
}
