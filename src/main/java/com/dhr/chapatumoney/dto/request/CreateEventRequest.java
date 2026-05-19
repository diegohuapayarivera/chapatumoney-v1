package com.dhr.chapatumoney.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CreateEventRequest {
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 300, message = "El nombre debe tener entre 3 y 300 caracteres")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "El lugar es requerido")
    @Size(min = 2, max = 300)
    private String lugar;

    @NotBlank(message = "La ciudad es requerida")
    @Size(min = 2, max = 150)
    private String ciudad;

    @NotNull(message = "La fecha de inicio es requerida")
    private OffsetDateTime fechaInicio;

    private OffsetDateTime fechaFin;
    private String imagenUrl;
}
