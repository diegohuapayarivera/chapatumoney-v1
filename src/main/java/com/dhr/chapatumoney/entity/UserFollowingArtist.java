package com.dhr.chapatumoney.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_following_artists", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollowingArtist {

    @EmbeddedId
    private UserFollowingArtistId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "followed_at", nullable = false, updatable = false)
    private OffsetDateTime followedAt;

    @PrePersist
    protected void onCreate() {
        if (followedAt == null) followedAt = OffsetDateTime.now();
    }
}
