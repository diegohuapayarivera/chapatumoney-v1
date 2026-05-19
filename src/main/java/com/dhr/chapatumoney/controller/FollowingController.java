package com.dhr.chapatumoney.controller;

import com.dhr.chapatumoney.dto.response.ArtistResponse;
import com.dhr.chapatumoney.dto.response.FollowingResponse;
import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.service.FollowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FollowingController {

    private final FollowingService followingService;

    /** POST /api/artists/{id}/follow */
    @PostMapping("/api/artists/{id}/follow")
    public ResponseEntity<FollowingResponse> followArtist(
            @PathVariable UUID id,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(followingService.followArtist(id, auth.getName()));
    }

    /** DELETE /api/artists/{id}/follow */
    @DeleteMapping("/api/artists/{id}/follow")
    public ResponseEntity<Void> unfollowArtist(
            @PathVariable UUID id,
            Authentication auth) {
        followingService.unfollowArtist(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    /** GET /api/users/me/following */
    @GetMapping("/api/users/me/following")
    public ResponseEntity<PagedResponse<ArtistResponse>> getFollowing(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        return ResponseEntity.ok(
                followingService.getFollowingArtists(auth.getName(), page, size));
    }
}
