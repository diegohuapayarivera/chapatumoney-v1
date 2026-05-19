package com.dhr.chapatumoney.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AuthProfileResponse {
    private UUID id;
    private String email;
    private String nombre;
    private String avatarUrl;
    private boolean isOrganizer;
    private boolean isArtist;
    private UUID organizerId;
    private UUID artistId;
    private OffsetDateTime createdAt;
}
