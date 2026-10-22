package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.Role;
import com.dem_ams.domain.model.UserModel;

import java.time.LocalDateTime;
import java.util.Set;

public record RegisterResponse(
        Long id,
        String username,
        String email,
        Set<Role> roles,
        LocalDateTime created_at,
        LocalDateTime updated_at) {
    public static RegisterResponse from(UserModel user) {
        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
