package com.dhr.chapatumoney.service;

import com.dhr.chapatumoney.dto.response.AuthProfileResponse;
import com.dhr.chapatumoney.entity.Artist;
import com.dhr.chapatumoney.entity.Organizer;
import com.dhr.chapatumoney.entity.User;
import com.dhr.chapatumoney.exception.ResourceNotFoundException;
import com.dhr.chapatumoney.repository.ArtistRepository;
import com.dhr.chapatumoney.repository.OrganizerRepository;
import com.dhr.chapatumoney.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizerRepository organizerRepository;
    private final ArtistRepository artistRepository;

    @Transactional(readOnly = true)
    public AuthProfileResponse getMyProfile(String userId) {
        UUID uuid = UUID.fromString(userId);

        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Optional<Organizer> organizer = organizerRepository.findByUserId(uuid);
        Optional<Artist> artist = artistRepository.findByUserId(uuid);

        return AuthProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nombre(user.getNombre())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isOrganizer(organizer.isPresent())
                .isArtist(artist.isPresent())
                .organizerId(organizer.map(Organizer::getId).orElse(null))
                .artistId(artist.map(Artist::getId).orElse(null))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
