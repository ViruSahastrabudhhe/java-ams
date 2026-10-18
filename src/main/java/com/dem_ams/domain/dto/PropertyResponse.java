package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.enums.BillingType;
import com.dem_ams.domain.model.Property;
import jakarta.annotation.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

public record PropertyResponse(
        Long id,
        Long landlord_profile_id,
        String name,
        String property_type,
        Double property_value,
        BillingType billing_type,
        Double size,
        String description,
        LocalDateTime created_at,
        @Nullable LocalDateTime updated_at,
        @Nullable LocalDateTime deleted_at
) implements Serializable {

    public static PropertyResponse from(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getLandlordProfile().getId(),
                property.getName(),
                property.getPropertyType(),
                property.getPropertyValue(),
                property.getBillingType(),
                property.getSize(),
                property.getDescription(),
                property.getCreatedAt(),
                property.getUpdatedAt(),
                property.getDeletedAt()
        );
    }
}
