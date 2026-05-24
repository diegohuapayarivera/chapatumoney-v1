package com.dhr.chapatumoney.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class TicketResponse {
    private UUID id;
    private UUID orderId;
    private String codigoQr;
    private boolean usado;
    private EventSummaryResponse evento;
    private String ticketTypeNombre;
    private OffsetDateTime createdAt;
    private String asiento;
}
