package com.dhr.chapatumoney.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderRequest {
    @NotNull(message = "El ticket_type_id es requerido")
    private UUID ticketTypeId;

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Max(value = 10, message = "La cantidad máxima es 10")
    private Integer cantidad;

    private String numeroConadis;
}
