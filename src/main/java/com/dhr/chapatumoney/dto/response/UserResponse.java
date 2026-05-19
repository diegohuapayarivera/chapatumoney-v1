package com.dhr.chapatumoney.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String nombre;
    private String avatarUrl;
    private OffsetDateTime createdAt;
}
