package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.LandlordProfile;
import jakarta.annotation.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

public record LandlordProfileResponse(
        Long id,
        String first_name,
        String middle_name,
        String last_name,
        Long user_id,
        LocalDateTime created_at,
        @Nullable LocalDateTime updated_at,
        @Nullable LocalDateTime deleted_at
) implements Serializable {
    public static LandlordProfileResponse from(LandlordProfile landlordProfile) {
        return new LandlordProfileResponse(
                landlordProfile.getId(),
                landlordProfile.getFirstName(),
                landlordProfile.getMiddleName(),
                landlordProfile.getLastName(),
                landlordProfile.getUser().getId(),
                landlordProfile.getCreatedAt(),
                landlordProfile.getUpdatedAt(),
                landlordProfile.getDeletedAt()
        );
    }
}
