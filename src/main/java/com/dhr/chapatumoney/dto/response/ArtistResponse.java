package com.dhr.chapatumoney.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ArtistResponse {
    private UUID id;
    private String slug;
    private String nombre;
    private String genero;
    private String bio;
    private String fotoUrl;
    private int followersCount;
    /**
     * TRUE if authenticated user follows this artist.
     * NULL if no session is present.
     */
    private Boolean isFollowing;
    private OffsetDateTime createdAt;
}
