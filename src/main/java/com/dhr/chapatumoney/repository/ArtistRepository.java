package com.dhr.chapatumoney.repository;

import com.dhr.chapatumoney.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    Optional<Artist> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    Optional<Artist> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("""
        SELECT a FROM Artist a
        WHERE (:q = '' OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:genero = '' OR LOWER(a.genero) LIKE LOWER(CONCAT('%', :genero, '%')))
        """)
    Page<Artist> searchArtists(
            @Param("q") String q,
            @Param("genero") String genero,
            Pageable pageable);
}
