package com.dhr.chapatumoney.controller;

import com.dhr.chapatumoney.dto.request.CreateArtistRequest;
import com.dhr.chapatumoney.dto.request.UpdateArtistRequest;
import com.dhr.chapatumoney.dto.response.ArtistResponse;
import com.dhr.chapatumoney.dto.response.EventSummaryResponse;
import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.service.ArtistService;
import com.dhr.chapatumoney.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<PagedResponse<ArtistResponse>> getArtists(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String genero,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        String userId = auth != null ? auth.getName() : null;
        return ResponseEntity.ok(artistService.searchArtists(q, genero, page, size, userId));
    }

    @PostMapping
    public ResponseEntity<ArtistResponse> createArtist(
            @Valid @RequestBody CreateArtistRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(artistService.createArtist(request, auth.getName()));
    }

    @GetMapping("/{idOrSlug}")
    public ResponseEntity<ArtistResponse> getArtist(
            @PathVariable String idOrSlug,
            Authentication auth) {
        String userId = auth != null ? auth.getName() : null;
        try {
            UUID id = UUID.fromString(idOrSlug);
            return ResponseEntity.ok(artistService.getArtist(id, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(artistService.getArtistBySlug(idOrSlug, userId));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ArtistResponse> updateArtist(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateArtistRequest request,
            Authentication auth) {
        return ResponseEntity.ok(artistService.updateArtist(id, request, auth.getName()));
    }

    @GetMapping("/{idOrSlug}/events")
    public ResponseEntity<PagedResponse<EventSummaryResponse>> getArtistEvents(
            @PathVariable String idOrSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        UUID id;
        try {
            id = UUID.fromString(idOrSlug);
        } catch (IllegalArgumentException e) {
            id = artistService.getArtistBySlug(idOrSlug, null).getId();
        }
        
        return ResponseEntity.ok(eventService.getEventsByArtist(id, page, size));
    }
}
