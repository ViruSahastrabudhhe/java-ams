package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.BillingType;
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
        Integer bedroom_quantity,
        Integer bathroom_quantity,
        String size,
        String description,
        LocalDateTime created_at,
        @Nullable LocalDateTime updated_at,
        @Nullable LocalDateTime deleted_at
) implements Serializable {

    public static PropertyResponse from(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getLandlordProfile().getId(),
                property.getPropertyDetail().getName(),
                property.getPropertyDetail().getPropertyType(),
                property.getPropertyDetail().getPropertyValue(),
                property.getPropertyDetail().getBillingType(),
                property.getPropertyDetail().getBedroomQuantity(),
                property.getPropertyDetail().getBathroomQuantity(),
                property.getPropertyDetail().getSize(),
                property.getPropertyDetail().getDescription(),
                property.getPropertyDetail().getCreatedAt(),
                property.getPropertyDetail().getUpdatedAt(),
                property.getPropertyDetail().getDeletedAt()
        );
    }
}
