package com.dhr.chapatumoney.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddArtistToEventRequest {
    @NotNull(message = "El artist_id es requerido")
    private UUID artistId;
    private Integer orden;
}
