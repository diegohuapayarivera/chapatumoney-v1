package com.dhr.chapatumoney.service;

import com.dhr.chapatumoney.dto.response.AdminStatsResponse;
import com.dhr.chapatumoney.dto.response.EventDetailResponse;
import com.dhr.chapatumoney.entity.Event;
import com.dhr.chapatumoney.entity.EventStatus;
import com.dhr.chapatumoney.entity.OrderStatus;
import com.dhr.chapatumoney.exception.ResourceNotFoundException;
import com.dhr.chapatumoney.repository.EventRepository;
import com.dhr.chapatumoney.repository.OrderRepository;
import com.dhr.chapatumoney.repository.OrganizerRepository;
import com.dhr.chapatumoney.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final OrganizerRepository organizerRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final EventService eventService; // To reuse toDetailResponse

    @Transactional(readOnly = true)
    public AdminStatsResponse getGlobalStats() {
        long totalUsers = userRepository.count();
        long totalOrganizers = organizerRepository.count();
        long totalEvents = eventRepository.count();
        
        Long ticketsSold = orderRepository.sumCantidadByEstado(OrderStatus.confirmed);
        BigDecimal totalRevenue = orderRepository.sumTotalByEstado(OrderStatus.confirmed);
        
        // 10% commission rule
        BigDecimal platformCommission = totalRevenue.multiply(new BigDecimal("0.10"));

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalOrganizers(totalOrganizers)
                .totalEvents(totalEvents)
                .totalTicketsSold(ticketsSold != null ? ticketsSold : 0L)
                .totalRevenue(totalRevenue)
                .totalPlatformCommission(platformCommission)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<EventDetailResponse> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(event -> eventService.toDetailResponse(event, null));
    }

    @Transactional
    public EventDetailResponse updateEventStatus(UUID eventId, EventStatus newStatus) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        event.setEstado(newStatus);
        return eventService.toDetailResponse(eventRepository.save(event), null);
    }
}
