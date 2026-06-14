package com.dhr.chapatumoney.service;

import com.dhr.chapatumoney.dto.request.AddArtistToEventRequest;
import com.dhr.chapatumoney.dto.request.CreateEventRequest;
import com.dhr.chapatumoney.dto.request.PublishEventRequest;
import com.dhr.chapatumoney.dto.request.UpdateEventRequest;
import com.dhr.chapatumoney.dto.response.*;
import com.dhr.chapatumoney.entity.*;
import com.dhr.chapatumoney.exception.*;
import com.dhr.chapatumoney.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final ArtistRepository artistRepository;
    private final EventArtistRepository eventArtistRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final OrderRepository orderRepository;
    private final UserFollowingArtistRepository followingRepository;

    // =====================================================================
    // PUBLIC: list events
    // =====================================================================
    @Transactional(readOnly = true)
    public PagedResponse<EventSummaryResponse> searchEvents(String ciudad, String genero,
                                                             String fechaDesde, String fechaHasta,
                                                             String q, int page, int size) {
        OffsetDateTime from = parseDate(fechaDesde, false);
        if (from == null) from = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        OffsetDateTime to = parseDate(fechaHasta, true);
        if (to == null) to = OffsetDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

        String safeCiudad = (ciudad == null) ? "" : ciudad;
        String safeGenero = (genero == null) ? "" : genero;
        String safeQ = (q == null) ? "" : q;

        Page<Event> events = eventRepository.searchPublishedEvents(
                safeCiudad, safeGenero, from, to, safeQ, PageRequest.of(page, size));

        return PagedResponse.from(events, toSummaryResponsesBulk(events.getContent(), null));
    }

    // =====================================================================
    // PUBLIC: event detail
    // =====================================================================
    @Transactional(readOnly = true)
    public EventDetailResponse getEvent(UUID id) {
        Event event = findById(id);
        return toDetailResponse(event, null);
    }

    // =====================================================================
    // ORGANIZER: create event
    // =====================================================================
    @Transactional
    public EventDetailResponse createEvent(CreateEventRequest request, String userId) {
        UUID uuid = UUID.fromString(userId);
        Organizer organizer = organizerRepository.findByUserId(uuid)
                .orElseThrow(() -> new UnauthorizedException("El usuario no tiene perfil de organizador"));

        Event event = Event.builder()
                .organizer(organizer)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .lugar(request.getLugar())
                .ciudad(request.getCiudad())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .imagenUrl(request.getImagenUrl())
                .estado(EventStatus.draft)
                .build();

        return toDetailResponse(eventRepository.save(event), userId);
    }

    // =====================================================================
    // ORGANIZER: update event
    // =====================================================================
    @Transactional
    public EventDetailResponse updateEvent(UUID id, UpdateEventRequest request, String userId) {
        Event event = findById(id);
        checkOwnership(event, userId);

        if (event.getEstado() == EventStatus.cancelled) {
            throw new BusinessRuleException("No se puede editar un evento cancelado");
        }

        if (request.getNombre() != null) event.setNombre(request.getNombre());
        if (request.getDescripcion() != null) event.setDescripcion(request.getDescripcion());
        if (request.getLugar() != null) event.setLugar(request.getLugar());
        if (request.getCiudad() != null) event.setCiudad(request.getCiudad());
        if (request.getFechaInicio() != null) event.setFechaInicio(request.getFechaInicio());
        if (request.getFechaFin() != null) event.setFechaFin(request.getFechaFin());
        if (request.getImagenUrl() != null) event.setImagenUrl(request.getImagenUrl());

        return toDetailResponse(eventRepository.save(event), userId);
    }

    // =====================================================================
    // ORGANIZER: publish event (draft → published)
    // =====================================================================
    @Transactional
    public EventDetailResponse publishEvent(UUID id, PublishEventRequest request, String userId) {
        Event event = findById(id);
        checkOwnership(event, userId);

        List<TicketType> types = ticketTypeRepository.findByEventId(id);
        if (types.isEmpty()) {
            throw new BusinessRuleException("El evento debe tener al menos un tipo de boleto antes de publicarse");
        }

        event.setEstado(EventStatus.published);
        return toDetailResponse(eventRepository.save(event), userId);
    }

    // =====================================================================
    // ORGANIZER: cancel event
    // =====================================================================
    @Transactional
    public void cancelEvent(UUID id, String userId) {
        Event event = findById(id);
        checkOwnership(event, userId);

        boolean hasConfirmedOrders = orderRepository.existsByTicketTypeEventIdAndEstado(id, OrderStatus.confirmed);
        if (hasConfirmedOrders) {
            throw new ConflictException("No se puede cancelar: el evento tiene órdenes confirmadas");
        }

        event.setEstado(EventStatus.cancelled);
        eventRepository.save(event);
    }

    // =====================================================================
    // ORGANIZER: add artist to event
    // =====================================================================
    @Transactional
    public EventDetailResponse addArtistToEvent(UUID eventId, AddArtistToEventRequest request, String userId) {
        Event event = findById(eventId);
        checkOwnership(event, userId);

        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con id: " + request.getArtistId()));

        if (eventArtistRepository.existsByIdEventIdAndIdArtistId(eventId, artist.getId())) {
            throw new ConflictException("El artista ya está en este evento");
        }

        EventArtistId compositeId = new EventArtistId(eventId, artist.getId());
        EventArtist ea = EventArtist.builder()
                .id(compositeId)
                .event(event)
                .artist(artist)
                .orden(request.getOrden())
                .build();

        eventArtistRepository.save(ea);
        return toDetailResponse(findById(eventId), userId);
    }

    // =====================================================================
    // ORGANIZER: remove artist from event
    // =====================================================================
    @Transactional
    public void removeArtistFromEvent(UUID eventId, UUID artistId, String userId) {
        Event event = findById(eventId);
        checkOwnership(event, userId);

        EventArtistId compositeId = new EventArtistId(eventId, artistId);
        if (!eventArtistRepository.existsById(compositeId)) {
            throw new ResourceNotFoundException("El artista no está en este evento");
        }
        eventArtistRepository.deleteById(compositeId);
    }

    // =====================================================================
    // PUBLIC: events by organizer
    // =====================================================================
    @Transactional(readOnly = true)
    public PagedResponse<EventSummaryResponse> getEventsByOrganizer(UUID organizerId,
                                                                      EventStatus estado, String timeFilter, int page, int size) {
        if (!organizerRepository.existsById(organizerId)) {
            throw new ResourceNotFoundException("Organizador no encontrado con id: " + organizerId);
        }
        Page<Event> events = eventRepository.findByOrganizerIdAndEstado(
                organizerId, estado, timeFilter, OffsetDateTime.now(), PageRequest.of(page, size));
        return PagedResponse.from(events, toSummaryResponsesBulk(events.getContent(), null));
    }

    // =====================================================================
    // PUBLIC: events by artist
    // =====================================================================
    @Transactional(readOnly = true)
    public PagedResponse<EventSummaryResponse> getEventsByArtist(UUID artistId, String timeFilter, int page, int size) {
        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException("Artista no encontrado con id: " + artistId);
        }
        Page<Event> events = eventRepository.findPublishedEventsByArtistId(
                artistId, timeFilter, OffsetDateTime.now(), PageRequest.of(page, size));
        return PagedResponse.from(events, toSummaryResponsesBulk(events.getContent(), null));
    }

    // =====================================================================
    // Helpers
    // =====================================================================
    private Event findById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));
    }

    private void checkOwnership(Event event, String userId) {
        if (!event.getOrganizer().getUser().getId().toString().equals(userId)) {
            throw new UnauthorizedException("No estás autorizado para gestionar este evento");
        }
    }

    // =====================================================================
    // Mapping
    // =====================================================================
    private List<EventSummaryResponse> toSummaryResponsesBulk(List<Event> events, String authenticatedUserId) {
        if (events.isEmpty()) return List.of();

        List<UUID> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<EventAggregateProjection> aggregates = ticketTypeRepository.getAggregatesByEventIds(eventIds);
        Map<UUID, BigDecimal> minPriceMap = aggregates.stream()
                .filter(a -> a.getMinPrice() != null)
                .collect(Collectors.toMap(EventAggregateProjection::getEventId, EventAggregateProjection::getMinPrice, (v1, v2) -> v1));
        Map<UUID, Integer> disponiblesMap = aggregates.stream()
                .collect(Collectors.toMap(EventAggregateProjection::getEventId, EventAggregateProjection::getTotalDisponibles, (v1, v2) -> v1));

        List<UUID> artistIds = events.stream()
                .flatMap(e -> e.getEventArtists().stream())
                .map(ea -> ea.getArtist().getId())
                .distinct()
                .collect(Collectors.toList());

        Map<UUID, Long> followersMap = Map.of();
        if (!artistIds.isEmpty()) {
            List<ArtistFollowersProjection> followers = followingRepository.getFollowersByArtistIds(artistIds);
            followersMap = followers.stream()
                    .collect(Collectors.toMap(ArtistFollowersProjection::getArtistId, ArtistFollowersProjection::getFollowers, (v1, v2) -> v1));
        }

        Map<UUID, Long> finalFollowersMap = followersMap;
        return events.stream().map(event -> {
            BigDecimal minPrice = minPriceMap.get(event.getId());
            Integer disponibles = disponiblesMap.getOrDefault(event.getId(), 0);

            List<ArtistResponse> artists = event.getEventArtists().stream()
                    .map(ea -> buildArtistResponse(ea.getArtist(), finalFollowersMap.getOrDefault(ea.getArtist().getId(), 0L)))
                    .collect(Collectors.toList());

            return buildEventSummaryResponse(event, artists, minPrice, disponibles);
        }).collect(Collectors.toList());
    }

    public EventSummaryResponse toSummaryResponse(Event event, String authenticatedUserId) {
        return toSummaryResponsesBulk(List.of(event), authenticatedUserId).get(0);
    }

    private ArtistResponse buildArtistResponse(Artist a, long followers) {
        return ArtistResponse.builder()
                .id(a.getId())
                .nombre(a.getNombre())
                .genero(a.getGenero())
                .bio(a.getBio())
                .fotoUrl(a.getFotoUrl())
                .followersCount((int) followers)
                .isFollowing(null)
                .createdAt(a.getCreatedAt())
                .build();
    }

    private EventSummaryResponse buildEventSummaryResponse(Event event, List<ArtistResponse> artists, BigDecimal minPrice, Integer disponibles) {
        return EventSummaryResponse.builder()
                .id(event.getId())
                .nombre(event.getNombre())
                .lugar(event.getLugar())
                .ciudad(event.getCiudad())
                .fechaInicio(event.getFechaInicio())
                .imagenUrl(event.getImagenUrl())
                .estado(event.getEstado())
                .organizer(OrganizerService.toResponse(event.getOrganizer()))
                .artists(artists)
                .precioDesde(minPrice)
                .boletosDisponibles(disponibles)
                .build();
    }

    public EventDetailResponse toDetailResponse(Event event, String authenticatedUserId) {
        EventSummaryResponse summary = toSummaryResponse(event, authenticatedUserId);

        List<TicketTypeResponse> ticketTypes = ticketTypeRepository.findByEventId(event.getId())
                .stream()
                .map(TicketTypeService::toResponse)
                .collect(Collectors.toList());

        return EventDetailResponse.builder()
                .id(summary.getId())
                .nombre(summary.getNombre())
                .lugar(summary.getLugar())
                .ciudad(summary.getCiudad())
                .descripcion(event.getDescripcion())
                .fechaInicio(summary.getFechaInicio())
                .fechaFin(event.getFechaFin())
                .imagenUrl(summary.getImagenUrl())
                .estado(summary.getEstado())
                .organizer(summary.getOrganizer())
                .artists(summary.getArtists())
                .precioDesde(summary.getPrecioDesde())
                .boletosDisponibles(summary.getBoletosDisponibles())
                .ticketTypes(ticketTypes)
                .createdAt(event.getCreatedAt())
                .build();
    }

    private OffsetDateTime parseDate(String dateStr, boolean endOfDay) {
        if (dateStr == null || dateStr.isBlank()) return null;
        LocalDate date = LocalDate.parse(dateStr);
        if (endOfDay) {
            return date.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);
        }
        return date.atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}
