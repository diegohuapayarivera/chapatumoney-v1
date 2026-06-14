package com.dhr.chapatumoney.repository;

import java.util.UUID;

public interface ArtistFollowersProjection {
    UUID getArtistId();
    Long getFollowers();
}
