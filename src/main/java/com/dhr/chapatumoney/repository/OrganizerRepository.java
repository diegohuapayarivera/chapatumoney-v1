package com.dhr.chapatumoney.repository;

import com.dhr.chapatumoney.entity.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, UUID> {

    Optional<Organizer> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    Optional<Organizer> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
