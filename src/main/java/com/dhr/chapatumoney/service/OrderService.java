package com.dhr.chapatumoney.service;

import com.dhr.chapatumoney.dto.request.CreateOrderRequest;
import com.dhr.chapatumoney.dto.response.OrderResponse;
import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.dto.response.TicketResponse;
import com.dhr.chapatumoney.entity.*;
import com.dhr.chapatumoney.exception.*;
import com.dhr.chapatumoney.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final TicketTypeService ticketTypeService;

    // =====================================================================
    // Create order (pending)
    // =====================================================================
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String userId) {
        UUID userUuid = UUID.fromString(userId);

        TicketType ticketType = ticketTypeRepository.findById(request.getTicketTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de boleto no encontrado con id: " + request.getTicketTypeId()));

        // RN-02: event must be published
        if (ticketType.getEvent().getEstado() != EventStatus.published) {
            throw new BusinessRuleException("Solo se pueden comprar boletos para eventos publicados");
        }

        // RN-01: check availability
        int disponibles = ticketType.getCapacidad() - ticketType.getVendidos();
        if (disponibles < request.getCantidad()) {
            throw new ConflictException("No hay suficientes boletos disponibles. Disponibles: " + disponibles);
        }

        // Validar si requiere carnet CONADIS
        if (Boolean.TRUE.equals(ticketType.getRequiereConadis())) {
            String numConadis = request.getNumeroConadis();
            if (numConadis == null || numConadis.trim().isEmpty()) {
                throw new BusinessRuleException("Se requiere un número de carnet CONADIS para comprar este tipo de boleto.");
            }
            if (!numConadis.trim().matches("^[a-zA-Z0-9-]{3,20}$")) {
                throw new BusinessRuleException("El número de carnet CONADIS ingresado no tiene un formato válido (ej. D-123456).");
            }
        }

        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        BigDecimal total = ticketType.getPrecio().multiply(BigDecimal.valueOf(request.getCantidad()));

        Order order = Order.builder()
                .user(user)
                .ticketType(ticketType)
                .cantidad(request.getCantidad())
                .total(total)
                .estado(OrderStatus.pending)
                .numeroConadis(Boolean.TRUE.equals(ticketType.getRequiereConadis()) ? request.getNumeroConadis().trim() : null)
                .build();

        return toResponse(orderRepository.save(order));
    }

    // =====================================================================
    // Confirm order → generate tickets + increment vendidos
    // =====================================================================
    @Transactional
    public OrderResponse confirmOrder(UUID orderId, String userId) {
        Order order = findById(orderId);

        // Ownership check
        if (!order.getUser().getId().toString().equals(userId)) {
            throw new UnauthorizedException("No estás autorizado para confirmar esta orden");
        }

        // State check
        if (order.getEstado() != OrderStatus.pending) {
            throw new ConflictException("La orden ya fue " + order.getEstado().name() + " y no puede confirmarse");
        }

        // Change state
        order.setEstado(OrderStatus.confirmed);

        // Increment vendidos
        TicketType ticketType = order.getTicketType();
        int initialVendidos = ticketType.getVendidos();
        ticketType.setVendidos(initialVendidos + order.getCantidad());
        ticketTypeRepository.save(ticketType);

        // Generate N tickets
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < order.getCantidad(); i++) {
            String seat = null;
            if (Boolean.TRUE.equals(ticketType.getConNumeracion())) {
                seat = "Asiento " + (initialVendidos + i + 1);
            }
            Ticket ticket = Ticket.builder()
                    .order(order)
                    .codigoQr(UUID.randomUUID().toString())
                    .usado(false)
                    .asiento(seat)
                    .build();
            tickets.add(ticket);
        }
        ticketRepository.saveAll(tickets);
        if (order.getTickets() == null) {
            order.setTickets(new ArrayList<>());
        }
        order.getTickets().clear();
        order.getTickets().addAll(tickets);

        return toResponse(orderRepository.save(order));
    }

    // =====================================================================
    // Cancel order (pending only)
    // =====================================================================
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String userId) {
        Order order = findById(orderId);

        if (!order.getUser().getId().toString().equals(userId)) {
            throw new UnauthorizedException("No estás autorizado para cancelar esta orden");
        }

        if (order.getEstado() != OrderStatus.pending) {
            throw new ConflictException("Solo se pueden cancelar órdenes en estado pending");
        }

        order.setEstado(OrderStatus.cancelled);
        return toResponse(orderRepository.save(order));
    }

    // =====================================================================
    // List my orders
    // =====================================================================
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getMyOrders(String userId, OrderStatus estado, int page, int size) {
        UUID uuid = UUID.fromString(userId);
        Page<Order> orders = orderRepository.findByUserIdAndEstado(uuid, estado, PageRequest.of(page, size));
        return PagedResponse.from(orders, this::toResponse);
    }

    // =====================================================================
    // Helpers
    // =====================================================================
    private Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));
    }

    private OrderResponse toResponse(Order order) {
        List<TicketResponse> ticketResponses = null;
        if (order.getEstado() == OrderStatus.confirmed) {
            ticketResponses = order.getTickets().stream()
                    .map(t -> toTicketResponse(t, order))
                    .collect(Collectors.toList());
        }

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .ticketType(TicketTypeService.toResponse(order.getTicketType()))
                .evento(eventService.toSummaryResponse(order.getTicketType().getEvent(), null))
                .cantidad(order.getCantidad())
                .total(order.getTotal())
                .estado(order.getEstado())
                .tickets(ticketResponses)
                .createdAt(order.getCreatedAt())
                .numeroConadis(order.getNumeroConadis())
                .build();
    }

    private TicketResponse toTicketResponse(Ticket ticket, Order order) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .orderId(order.getId())
                .codigoQr(ticket.getCodigoQr())
                .usado(ticket.getUsado())
                .evento(eventService.toSummaryResponse(order.getTicketType().getEvent(), null))
                .ticketTypeNombre(order.getTicketType().getNombre())
                .createdAt(ticket.getCreatedAt())
                .asiento(ticket.getAsiento())
                .build();
    }
}
