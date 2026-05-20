package com.dhr.chapatumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("is_organizer")
    private boolean isOrganizer;
    
    @JsonProperty("is_artist")
    private boolean isArtist;
    
    private UUID organizerId;
    private UUID artistId;
    private OffsetDateTime createdAt;
}
