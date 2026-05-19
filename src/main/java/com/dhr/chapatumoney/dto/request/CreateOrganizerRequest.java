package com.dhr.chapatumoney.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrganizerRequest {
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String nombre;
    private String descripcion;
    private String logoUrl;
}
