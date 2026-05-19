package com.dhr.chapatumoney.controller;

import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.dto.response.TicketResponse;
import com.dhr.chapatumoney.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/my")
    public ResponseEntity<PagedResponse<TicketResponse>> getMyTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        return ResponseEntity.ok(ticketService.getMyTickets(auth.getName(), page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(
            @PathVariable UUID id,
            Authentication auth) {
        return ResponseEntity.ok(ticketService.getTicket(id, auth.getName()));
    }
}
