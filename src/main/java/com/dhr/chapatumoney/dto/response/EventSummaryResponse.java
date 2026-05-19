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
public class EventSummaryResponse {
    private UUID id;
    private String nombre;
    private String lugar;
    private String ciudad;
    private OffsetDateTime fechaInicio;
    private String imagenUrl;
    private EventStatus estado;
    private OrganizerResponse organizer;
    private List<ArtistResponse> artists;
    /** MIN(precio) of all ticket_types */
    private BigDecimal precioDesde;
    /** SUM(capacidad - vendidos) of all ticket_types */
    private Integer boletosDisponibles;
}
