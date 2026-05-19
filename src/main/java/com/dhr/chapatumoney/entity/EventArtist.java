package com.dhr.chapatumoney.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_artists", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventArtist {

    @EmbeddedId
    private EventArtistId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "orden")
    private Integer orden;
}
