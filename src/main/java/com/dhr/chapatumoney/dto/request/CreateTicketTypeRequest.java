package com.dhr.chapatumoney.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTicketTypeRequest {
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 150)
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @NotNull(message = "La capacidad es requerida")
    @Min(value = 1, message = "La capacidad mínima es 1")
    private Integer capacidad;

    private Boolean conNumeracion;
    private Boolean requiereConadis;
}
