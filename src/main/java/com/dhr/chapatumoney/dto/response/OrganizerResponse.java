package com.dhr.chapatumoney.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class OrganizerResponse {
    private UUID id;
    private UUID userId;
    private String slug;
    private String nombre;
    private String descripcion;
    private String logoUrl;
    private OffsetDateTime createdAt;
}
