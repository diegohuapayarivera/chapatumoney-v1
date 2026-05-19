package com.dhr.chapatumoney.service;

import com.dhr.chapatumoney.dto.request.UpdateUserRequest;
import com.dhr.chapatumoney.dto.response.UserResponse;
import com.dhr.chapatumoney.entity.User;
import com.dhr.chapatumoney.exception.ResourceNotFoundException;
import com.dhr.chapatumoney.exception.UnauthorizedException;
import com.dhr.chapatumoney.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request, String authenticatedUserId) {
        if (!id.toString().equals(authenticatedUserId)) {
            throw new UnauthorizedException("No estás autorizado para editar este perfil");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (request.getNombre() != null) user.setNombre(request.getNombre());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

        return toResponse(userRepository.save(user));
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
