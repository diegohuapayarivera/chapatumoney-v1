package com.dhr.chapatumoney.dto.response;

import com.dhr.chapatumoney.entity.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class EventDetailResponse {
    // Inherited from EventSummaryResponse (flattened for simplicity)
    private UUID id;
    private String nombre;
    private String lugar;
    private String ciudad;
    private String descripcion;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFin;
    private String imagenUrl;
    private EventStatus estado;
    private OrganizerResponse organizer;
    private List<ArtistResponse> artists;
    private BigDecimal precioDesde;
    private Integer boletosDisponibles;
    private List<TicketTypeResponse> ticketTypes;
    private OffsetDateTime createdAt;
}
