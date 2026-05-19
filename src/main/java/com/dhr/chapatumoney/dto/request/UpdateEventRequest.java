package com.dhr.chapatumoney.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UpdateEventRequest {
    @Size(min = 3, max = 300)
    private String nombre;
    private String descripcion;
    private String lugar;
    private String ciudad;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFin;
    private String imagenUrl;
}
