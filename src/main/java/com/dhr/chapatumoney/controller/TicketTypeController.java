package com.dhr.chapatumoney.controller;

import com.dhr.chapatumoney.dto.request.CreateTicketTypeRequest;
import com.dhr.chapatumoney.dto.response.TicketTypeResponse;
import com.dhr.chapatumoney.service.TicketTypeService;
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
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @PostMapping("/{eventId}/ticket-types")
    public ResponseEntity<TicketTypeResponse> createTicketType(
            @PathVariable UUID eventId,
            @Valid @RequestBody CreateTicketTypeRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketTypeService.createTicketType(eventId, request, auth.getName()));
    }

    @DeleteMapping("/{eventId}/ticket-types/{ticketTypeId}")
    public ResponseEntity<Void> deleteTicketType(
            @PathVariable UUID eventId,
            @PathVariable UUID ticketTypeId,
            Authentication auth) {
        ticketTypeService.deleteTicketType(eventId, ticketTypeId, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
