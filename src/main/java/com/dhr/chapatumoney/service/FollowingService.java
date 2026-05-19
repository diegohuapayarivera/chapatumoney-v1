package com.dhr.chapatumoney.service;

import com.dhr.chapatumoney.dto.response.ArtistResponse;
import com.dhr.chapatumoney.dto.response.FollowingResponse;
import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.entity.Artist;
import com.dhr.chapatumoney.entity.User;
import com.dhr.chapatumoney.entity.UserFollowingArtist;
import com.dhr.chapatumoney.entity.UserFollowingArtistId;
import com.dhr.chapatumoney.exception.ConflictException;
import com.dhr.chapatumoney.exception.ResourceNotFoundException;
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
public class FollowingService {

    private final UserFollowingArtistRepository followingRepository;
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final ArtistService artistService;

    @Transactional
    public FollowingResponse followArtist(UUID artistId, String userId) {
        UUID userUuid = UUID.fromString(userId);

        if (followingRepository.existsByIdUserIdAndIdArtistId(userUuid, artistId)) {
            throw new ConflictException("Ya sigues a este artista");
        }

        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con id: " + artistId));

        UserFollowingArtistId id = new UserFollowingArtistId(userUuid, artistId);
        UserFollowingArtist following = UserFollowingArtist.builder()
                .id(id)
                .user(user)
                .artist(artist)
                .build();

        UserFollowingArtist saved = followingRepository.save(following);

        return FollowingResponse.builder()
                .userId(saved.getUser().getId())
                .artistId(saved.getArtist().getId())
                .followedAt(saved.getFollowedAt())
                .build();
    }

    @Transactional
    public void unfollowArtist(UUID artistId, String userId) {
        UUID userUuid = UUID.fromString(userId);
        UserFollowingArtistId id = new UserFollowingArtistId(userUuid, artistId);

        if (!followingRepository.existsById(id)) {
            throw new ResourceNotFoundException("No sigues a este artista");
        }

        followingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ArtistResponse> getFollowingArtists(String userId, int page, int size) {
        UUID userUuid = UUID.fromString(userId);
        Page<UserFollowingArtist> followings = followingRepository.findByUserId(
                userUuid, PageRequest.of(page, size));

        return PagedResponse.from(followings, uf -> artistService.toResponse(uf.getArtist(), userId));
    }
}
