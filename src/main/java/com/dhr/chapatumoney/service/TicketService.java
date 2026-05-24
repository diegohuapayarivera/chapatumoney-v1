package com.dhr.chapatumoney.service;

import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.dto.response.TicketResponse;
import com.dhr.chapatumoney.entity.Ticket;
import com.dhr.chapatumoney.exception.ResourceNotFoundException;
import com.dhr.chapatumoney.exception.UnauthorizedException;
import com.dhr.chapatumoney.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventService eventService;

    @Transactional(readOnly = true)
    public PagedResponse<TicketResponse> getMyTickets(String userId, int page, int size) {
        UUID uuid = UUID.fromString(userId);
        Page<Ticket> tickets = ticketRepository.findByUserId(uuid, PageRequest.of(page, size));
        return PagedResponse.from(tickets, this::toResponse);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicket(UUID id, String userId) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleto no encontrado con id: " + id));

        if (!ticket.getOrder().getUser().getId().toString().equals(userId)) {
            throw new UnauthorizedException("No estás autorizado para ver este boleto");
        }

        return toResponse(ticket);
    }

    private TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .orderId(ticket.getOrder().getId())
                .codigoQr(ticket.getCodigoQr())
                .usado(ticket.getUsado())
                .evento(eventService.toSummaryResponse(ticket.getOrder().getTicketType().getEvent(), null))
                .ticketTypeNombre(ticket.getOrder().getTicketType().getNombre())
                .createdAt(ticket.getCreatedAt())
                .asiento(ticket.getAsiento())
                .build();
    }
}
