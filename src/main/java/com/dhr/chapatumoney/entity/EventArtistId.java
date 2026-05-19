package com.dhr.chapatumoney.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventArtistId implements Serializable {

    @Column(name = "event_id", columnDefinition = "uuid")
    private UUID eventId;

    @Column(name = "artist_id", columnDefinition = "uuid")
    private UUID artistId;
}
