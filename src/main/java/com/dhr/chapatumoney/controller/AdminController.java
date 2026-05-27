package com.dhr.chapatumoney.controller;

import com.dhr.chapatumoney.dto.response.AdminStatsResponse;
import com.dhr.chapatumoney.dto.response.EventDetailResponse;
import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.entity.EventStatus;
import com.dhr.chapatumoney.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dhr.chapatumoney.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    private void verifyAdmin(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            throw new org.springframework.security.access.AccessDeniedException("No autenticado");
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        UUID userId = UUID.fromString(jwt.getSubject());
        userRepository.findById(userId)
                .filter(u -> "ADMIN".equalsIgnoreCase(u.getRole()))
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Acceso denegado: Se requiere rol de Administrador"));
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getGlobalStats(Authentication authentication) {
        verifyAdmin(authentication);
        return ResponseEntity.ok(adminService.getGlobalStats());
    }

    @GetMapping("/events")
    public ResponseEntity<PagedResponse<EventDetailResponse>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        verifyAdmin(authentication);
        Page<EventDetailResponse> pageData = adminService.getAllEvents(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return ResponseEntity.ok(PagedResponse.from(pageData));
    }

    @PatchMapping("/events/{id}/status")
    public ResponseEntity<EventDetailResponse> updateEventStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        verifyAdmin(authentication);
        String statusStr = body.get("estado");
        if (statusStr == null) {
            throw new IllegalArgumentException("Se requiere el campo 'estado' en el cuerpo.");
        }
        EventStatus status = EventStatus.valueOf(statusStr.toLowerCase());
        return ResponseEntity.ok(adminService.updateEventStatus(id, status));
    }
}
