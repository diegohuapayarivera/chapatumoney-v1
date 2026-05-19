package com.dhr.chapatumoney.service;

import com.dhr.chapatumoney.dto.request.CreateTicketTypeRequest;
import com.dhr.chapatumoney.dto.response.TicketTypeResponse;
import com.dhr.chapatumoney.entity.Event;
import com.dhr.chapatumoney.entity.OrderStatus;
import com.dhr.chapatumoney.entity.TicketType;
import com.dhr.chapatumoney.exception.ConflictException;
import com.dhr.chapatumoney.exception.ResourceNotFoundException;
import com.dhr.chapatumoney.exception.UnauthorizedException;
import com.dhr.chapatumoney.repository.EventRepository;
import com.dhr.chapatumoney.repository.OrderRepository;
import com.dhr.chapatumoney.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public TicketTypeResponse createTicketType(UUID eventId, CreateTicketTypeRequest request, String userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + eventId));

        if (!event.getOrganizer().getUser().getId().toString().equals(userId)) {
            throw new UnauthorizedException("No estás autorizado para gestionar este evento");
        }

        TicketType ticketType = TicketType.builder()
                .event(event)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .capacidad(request.getCapacidad())
                .vendidos(0)
                .build();

        return toResponse(ticketTypeRepository.save(ticketType));
    }

    @Transactional
    public void deleteTicketType(UUID eventId, UUID ticketTypeId, String userId) {
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de boleto no encontrado con id: " + ticketTypeId));

        if (!ticketType.getEvent().getId().equals(eventId)) {
            throw new ResourceNotFoundException("El tipo de boleto no pertenece a este evento");
        }

        if (!ticketType.getEvent().getOrganizer().getUser().getId().toString().equals(userId)) {
            throw new UnauthorizedException("No estás autorizado para gestionar este evento");
        }

        boolean hasConfirmedOrders = orderRepository.existsByTicketTypeEventIdAndEstado(eventId, OrderStatus.confirmed);
        if (hasConfirmedOrders) {
            throw new ConflictException("El tipo de boleto tiene órdenes confirmadas y no puede eliminarse");
        }

        ticketTypeRepository.delete(ticketType);
    }

    public static TicketTypeResponse toResponse(TicketType t) {
        return TicketTypeResponse.builder()
                .id(t.getId())
                .eventId(t.getEvent().getId())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .precio(t.getPrecio())
                .capacidad(t.getCapacidad())
                .vendidos(t.getVendidos())
                .disponibles(t.getCapacidad() - t.getVendidos())
                .build();
    }
}
