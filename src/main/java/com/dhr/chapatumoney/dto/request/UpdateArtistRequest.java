package com.dhr.chapatumoney.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateArtistRequest {
    @Size(min = 2, max = 200)
    private String nombre;
    @Size(min = 2, max = 100)
    private String genero;
    private String bio;
    private String fotoUrl;
}
