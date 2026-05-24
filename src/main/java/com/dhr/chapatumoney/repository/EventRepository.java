package com.dhr.chapatumoney.repository;

import com.dhr.chapatumoney.entity.Event;
import com.dhr.chapatumoney.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("""
        SELECT DISTINCT e FROM Event e
        LEFT JOIN e.eventArtists ea
        LEFT JOIN ea.artist a
        WHERE e.estado = 'published'
          AND (:ciudad = '' OR LOWER(e.ciudad) = LOWER(:ciudad))
          AND (:genero = '' OR LOWER(a.genero) LIKE LOWER(CONCAT('%', :genero, '%')))
          AND (e.fechaInicio >= :fechaDesde)
          AND (e.fechaInicio <= :fechaHasta)
          AND (:q = '' OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
                          OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :q, '%')))
        """)
    Page<Event> searchPublishedEvents(
            @Param("ciudad") String ciudad,
            @Param("genero") String genero,
            @Param("fechaDesde") OffsetDateTime fechaDesde,
            @Param("fechaHasta") OffsetDateTime fechaHasta,
            @Param("q") String q,
            Pageable pageable);

    @Query("""
        SELECT e FROM Event e
        WHERE e.organizer.id = :organizerId
          AND (:estado IS NULL OR e.estado = :estado)
          AND (:timeFilter IS NULL 
               OR (:timeFilter = 'upcoming' AND e.fechaInicio >= :now)
               OR (:timeFilter = 'past' AND e.fechaInicio < :now))
        """)
    Page<Event> findByOrganizerIdAndEstado(
            @Param("organizerId") UUID organizerId,
            @Param("estado") EventStatus estado,
            @Param("timeFilter") String timeFilter,
            @Param("now") OffsetDateTime now,
            Pageable pageable);

    @Query("""
        SELECT DISTINCT e FROM Event e
        JOIN e.eventArtists ea
        WHERE ea.artist.id = :artistId
          AND e.estado = 'published'
          AND (:timeFilter IS NULL 
               OR (:timeFilter = 'upcoming' AND e.fechaInicio >= :now)
               OR (:timeFilter = 'past' AND e.fechaInicio < :now))
        """)
    Page<Event> findPublishedEventsByArtistId(
            @Param("artistId") UUID artistId,
            @Param("timeFilter") String timeFilter,
            @Param("now") OffsetDateTime now,
            Pageable pageable);
}
