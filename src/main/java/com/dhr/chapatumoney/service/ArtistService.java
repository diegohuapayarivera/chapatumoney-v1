package com.dhr.chapatumoney.service;

import com.dhr.chapatumoney.dto.request.CreateArtistRequest;
import com.dhr.chapatumoney.dto.request.UpdateArtistRequest;
import com.dhr.chapatumoney.dto.response.ArtistResponse;
import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.entity.Artist;
import com.dhr.chapatumoney.entity.User;
import com.dhr.chapatumoney.exception.ConflictException;
import com.dhr.chapatumoney.exception.ResourceNotFoundException;
import com.dhr.chapatumoney.exception.UnauthorizedException;
import com.dhr.chapatumoney.repository.ArtistRepository;
import com.dhr.chapatumoney.repository.UserFollowingArtistRepository;
import com.dhr.chapatumoney.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final UserFollowingArtistRepository followingRepository;

    @Transactional
    public ArtistResponse createArtist(CreateArtistRequest request, String userId) {
        UUID uuid = UUID.fromString(userId);

        if (artistRepository.existsByUserId(uuid)) {
            throw new ConflictException("El usuario ya tiene un perfil de artista");
        }

        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Artist artist = Artist.builder()
                .user(user)
                .nombre(request.getNombre())
                .genero(request.getGenero())
                .bio(request.getBio())
                .fotoUrl(request.getFotoUrl())
                .build();

        return toResponse(artistRepository.save(artist), null);
    }

    @Transactional(readOnly = true)
    public ArtistResponse getArtist(UUID id, String authenticatedUserId) {
        Artist artist = findById(id);
        return toResponse(artist, authenticatedUserId);
    }

    @Transactional
    public ArtistResponse updateArtist(UUID id, UpdateArtistRequest request, String userId) {
        Artist artist = findById(id);

        if (artist.getUser() == null || !artist.getUser().getId().toString().equals(userId)) {
            throw new UnauthorizedException("No estás autorizado para editar este perfil de artista");
        }

        if (request.getNombre() != null) artist.setNombre(request.getNombre());
        if (request.getGenero() != null) artist.setGenero(request.getGenero());
        if (request.getBio() != null) artist.setBio(request.getBio());
        if (request.getFotoUrl() != null) artist.setFotoUrl(request.getFotoUrl());

        return toResponse(artistRepository.save(artist), userId);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ArtistResponse> searchArtists(String q, String genero, int page, int size,
                                                        String authenticatedUserId) {
        String safeQ = (q == null) ? "" : q;
        String safeGenero = (genero == null) ? "" : genero;
        Page<Artist> artistPage = artistRepository.searchArtists(safeQ, safeGenero, PageRequest.of(page, size));
        return PagedResponse.from(artistPage, a -> toResponse(a, authenticatedUserId));
    }

    public ArtistResponse toResponse(Artist artist, String authenticatedUserId) {
        long followers = followingRepository.countByIdArtistId(artist.getId());
        Boolean isFollowing = null;

        if (authenticatedUserId != null) {
            UUID userId = UUID.fromString(authenticatedUserId);
            isFollowing = followingRepository.existsByIdUserIdAndIdArtistId(userId, artist.getId());
        }

        return ArtistResponse.builder()
                .id(artist.getId())
                .nombre(artist.getNombre())
                .genero(artist.getGenero())
                .bio(artist.getBio())
                .fotoUrl(artist.getFotoUrl())
                .followersCount((int) followers)
                .isFollowing(isFollowing)
                .createdAt(artist.getCreatedAt())
                .build();
    }

    public Artist findById(UUID id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con id: " + id));
    }
}
