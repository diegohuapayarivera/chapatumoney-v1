package com.dhr.chapatumoney.controller;

import com.dhr.chapatumoney.dto.request.UpdateUserRequest;
import com.dhr.chapatumoney.dto.response.UserResponse;
import com.dhr.chapatumoney.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request,
            Authentication auth) {
        return ResponseEntity.ok(userService.updateUser(id, request, auth.getName()));
    }
}
