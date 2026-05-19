package com.dhr.chapatumoney.service;

import com.dhr.chapatumoney.dto.request.CreateOrganizerRequest;
import com.dhr.chapatumoney.dto.request.UpdateOrganizerRequest;
import com.dhr.chapatumoney.dto.response.OrganizerResponse;
import com.dhr.chapatumoney.entity.Organizer;
import com.dhr.chapatumoney.entity.User;
import com.dhr.chapatumoney.exception.ConflictException;
import com.dhr.chapatumoney.exception.ResourceNotFoundException;
import com.dhr.chapatumoney.exception.UnauthorizedException;
import com.dhr.chapatumoney.repository.OrganizerRepository;
import com.dhr.chapatumoney.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrganizerResponse createOrganizer(CreateOrganizerRequest request, String userId) {
        UUID uuid = UUID.fromString(userId);

        if (organizerRepository.existsByUserId(uuid)) {
            throw new ConflictException("El usuario ya tiene un perfil de organizador");
        }

        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Organizer organizer = Organizer.builder()
                .user(user)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .logoUrl(request.getLogoUrl())
                .build();

        return toResponse(organizerRepository.save(organizer));
    }

    @Transactional(readOnly = true)
    public OrganizerResponse getOrganizer(UUID id) {
        return toResponse(findById(id));
    }

    @Transactional
    public OrganizerResponse updateOrganizer(UUID id, UpdateOrganizerRequest request, String userId) {
        Organizer organizer = findById(id);

        if (!organizer.getUser().getId().toString().equals(userId)) {
            throw new UnauthorizedException("No estás autorizado para editar este perfil de organizador");
        }

        if (request.getNombre() != null) organizer.setNombre(request.getNombre());
        if (request.getDescripcion() != null) organizer.setDescripcion(request.getDescripcion());
        if (request.getLogoUrl() != null) organizer.setLogoUrl(request.getLogoUrl());

        return toResponse(organizerRepository.save(organizer));
    }

    private Organizer findById(UUID id) {
        return organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador no encontrado con id: " + id));
    }

    public static OrganizerResponse toResponse(Organizer o) {
        return OrganizerResponse.builder()
                .id(o.getId())
                .userId(o.getUser().getId())
                .nombre(o.getNombre())
                .descripcion(o.getDescripcion())
                .logoUrl(o.getLogoUrl())
                .createdAt(o.getCreatedAt())
                .build();
    }
}
