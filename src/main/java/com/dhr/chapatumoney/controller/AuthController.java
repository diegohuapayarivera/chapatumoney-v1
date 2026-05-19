package com.dhr.chapatumoney.controller;

import com.dhr.chapatumoney.dto.response.AuthProfileResponse;
import com.dhr.chapatumoney.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<AuthProfileResponse> getMyProfile(Authentication auth) {
        return ResponseEntity.ok(authService.getMyProfile(auth.getName()));
    }
}
