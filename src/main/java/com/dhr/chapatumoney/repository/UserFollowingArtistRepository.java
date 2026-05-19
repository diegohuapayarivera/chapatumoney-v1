package com.dhr.chapatumoney.repository;

import com.dhr.chapatumoney.entity.UserFollowingArtist;
import com.dhr.chapatumoney.entity.UserFollowingArtistId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserFollowingArtistRepository extends JpaRepository<UserFollowingArtist, UserFollowingArtistId> {

    boolean existsByIdUserIdAndIdArtistId(UUID userId, UUID artistId);

    long countByIdArtistId(UUID artistId);

    @Query("""
        SELECT uf FROM UserFollowingArtist uf
        WHERE uf.user.id = :userId
        """)
    Page<UserFollowingArtist> findByUserId(@Param("userId") UUID userId, Pageable pageable);
}
