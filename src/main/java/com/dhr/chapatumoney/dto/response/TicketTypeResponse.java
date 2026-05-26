package com.dhr.chapatumoney.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class TicketTypeResponse {
    private UUID id;
    private UUID eventId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int capacidad;
    private int vendidos;
    /** capacidad - vendidos */
    private int disponibles;
    private boolean conNumeracion;
    private boolean requiereConadis;
}
