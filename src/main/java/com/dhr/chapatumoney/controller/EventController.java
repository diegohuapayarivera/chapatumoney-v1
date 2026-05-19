package com.dhr.chapatumoney.controller;

import com.dhr.chapatumoney.dto.request.AddArtistToEventRequest;
import com.dhr.chapatumoney.dto.request.CreateEventRequest;
import com.dhr.chapatumoney.dto.request.PublishEventRequest;
import com.dhr.chapatumoney.dto.request.UpdateEventRequest;
import com.dhr.chapatumoney.dto.response.EventDetailResponse;
import com.dhr.chapatumoney.dto.response.EventSummaryResponse;
import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<PagedResponse<EventSummaryResponse>> getEvents(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) String fecha_desde,
            @RequestParam(required = false) String fecha_hasta,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                eventService.searchEvents(ciudad, genero, fecha_desde, fecha_hasta, q, page, size));
    }

    @PostMapping
    public ResponseEntity<EventDetailResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(request, auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDetailResponse> getEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EventDetailResponse> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventRequest request,
            Authentication auth) {
        return ResponseEntity.ok(eventService.updateEvent(id, request, auth.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelEvent(
            @PathVariable UUID id,
            Authentication auth) {
        eventService.cancelEvent(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<EventDetailResponse> publishEvent(
            @PathVariable UUID id,
            @Valid @RequestBody PublishEventRequest request,
            Authentication auth) {
        return ResponseEntity.ok(eventService.publishEvent(id, request, auth.getName()));
    }

    @PostMapping("/{id}/artists")
    public ResponseEntity<EventDetailResponse> addArtist(
            @PathVariable UUID id,
            @Valid @RequestBody AddArtistToEventRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.addArtistToEvent(id, request, auth.getName()));
    }

    @DeleteMapping("/{id}/artists/{artistId}")
    public ResponseEntity<Void> removeArtist(
            @PathVariable UUID id,
            @PathVariable UUID artistId,
            Authentication auth) {
        eventService.removeArtistFromEvent(id, artistId, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
