package com.dhr.chapatumoney.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateArtistRequest {
    @Pattern(regexp = "^[a-z0-9-]+$", message = "El slug solo puede contener letras minúsculas, números y guiones")
    private String slug;

    @Size(min = 2, max = 200)
    private String nombre;
    @Size(min = 2, max = 100)
    private String genero;
    private String bio;
    private String fotoUrl;
}
