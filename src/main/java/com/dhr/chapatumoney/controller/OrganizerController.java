package com.dhr.chapatumoney.controller;

import com.dhr.chapatumoney.dto.request.CreateOrganizerRequest;
import com.dhr.chapatumoney.dto.request.UpdateOrganizerRequest;
import com.dhr.chapatumoney.dto.response.EventSummaryResponse;
import com.dhr.chapatumoney.dto.response.OrganizerResponse;
import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.entity.EventStatus;
import com.dhr.chapatumoney.service.EventService;
import com.dhr.chapatumoney.service.OrganizerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/organizers")
@RequiredArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<OrganizerResponse> createOrganizer(
            @Valid @RequestBody CreateOrganizerRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizerService.createOrganizer(request, auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizerResponse> getOrganizer(@PathVariable UUID id) {
        return ResponseEntity.ok(organizerService.getOrganizer(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrganizerResponse> updateOrganizer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizerRequest request,
            Authentication auth) {
        return ResponseEntity.ok(organizerService.updateOrganizer(id, request, auth.getName()));
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<PagedResponse<EventSummaryResponse>> getOrganizerEvents(
            @PathVariable UUID id,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        EventStatus status = estado != null ? EventStatus.valueOf(estado) : null;
        return ResponseEntity.ok(eventService.getEventsByOrganizer(id, status, page, size));
    }
}
