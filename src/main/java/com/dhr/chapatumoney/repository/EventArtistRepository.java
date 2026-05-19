package com.dhr.chapatumoney.repository;

import com.dhr.chapatumoney.entity.EventArtist;
import com.dhr.chapatumoney.entity.EventArtistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventArtistRepository extends JpaRepository<EventArtist, EventArtistId> {

    List<EventArtist> findByEventIdOrderByOrdenAsc(UUID eventId);

    boolean existsByIdEventIdAndIdArtistId(UUID eventId, UUID artistId);
}
