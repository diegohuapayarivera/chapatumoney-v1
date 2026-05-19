package com.dhr.chapatumoney.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateOrganizerRequest {
    @Size(min = 2, max = 200)
    private String nombre;
    private String descripcion;
    private String logoUrl;
}
