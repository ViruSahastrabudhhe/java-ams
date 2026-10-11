package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.TenantProfile;
import jakarta.annotation.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

public record TenantProfileResponse(
        Long id,
        String first_name,
        String middle_name,
        String last_name,
        Long user_id,
        LocalDateTime created_at,
        @Nullable LocalDateTime updated_at,
        @Nullable LocalDateTime deleted_at
) implements Serializable {
    public static TenantProfileResponse from(TenantProfile tenantProfile) {
        return new TenantProfileResponse(
                tenantProfile.getId(),
                tenantProfile.getFirstName(),
                tenantProfile.getMiddleName(),
                tenantProfile.getLastName(),
                tenantProfile.getUser().getId(),
                tenantProfile.getCreatedAt(),
                tenantProfile.getUpdatedAt(),
                tenantProfile.getDeletedAt()
        );
    }
}
